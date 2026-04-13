package com.tqt.englishApp.service;

import com.tqt.englishApp.dto.request.VocabularyProgressRequest;
import com.tqt.englishApp.dto.response.vocabulary.UserVocabularyResponse;
import com.tqt.englishApp.entity.UserVocabularyProgress;
import com.tqt.englishApp.enums.VocabularyStatus;
import com.tqt.englishApp.mapper.UserVocabularyMapper;
import com.tqt.englishApp.repository.UserLearningProfileRepository;
import com.tqt.englishApp.repository.UserVocabularyProgressRepository;
import com.tqt.englishApp.repository.VocabularyMeaningRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class VocabularyLearningService {
    @Autowired
    private UserVocabularyProgressRepository userVocabularyProgressRepository;

    @Autowired
    private VocabularyMeaningRepository VocabularyMeaningRepository;

    @Autowired
    private UserLearningProfileRepository userLearningProfileRepository;

    @Autowired
    private UserVocabularyMapper userVocabularyMapper;

    @Autowired
    private FsrsService fsrsService;

    public UserVocabularyResponse updateVocabularyProgress(VocabularyProgressRequest request) {
        UserVocabularyProgress progress = userVocabularyProgressRepository
                .findByUserIdAndMeaningId(request.getUserId(), request.getMeaningId())
                .orElse(UserVocabularyProgress.builder()
                        .userId(request.getUserId())
                        .meaning(VocabularyMeaningRepository.findById(request.getMeaningId()).orElse(null))
                        .build());

        LocalDateTime now = LocalDateTime.now();
        boolean isDue = progress.getNextReviewAt() == null || now.isAfter(progress.getNextReviewAt());

        if (request.getIsCorrect() != null) {
            updateSrsData(progress, request.getIsCorrect(), request.getResponseTime(), now, isDue);
            progress.setLastReviewedAt(now);
        }

        updateVocabularyStatus(progress, isDue);

        progress.setUpdatedAt(now);
        UserVocabularyProgress savedProgress = userVocabularyProgressRepository.save(progress);

        if (progress.getStatus() == VocabularyStatus.MASTERED) {
            checkAndUpgradeLevel(request.getUserId());
        }

        return userVocabularyMapper.toUserVocabularyResponse(savedProgress);
    }

    private void updateSrsData(UserVocabularyProgress progress, boolean isCorrect, Long responseTime, LocalDateTime now, boolean isDue) {
        // Luôn theo dõi tổng số lần đúng/sai
        if (isCorrect) {
            progress.setCorrectCount(progress.getCorrectCount() + 1);
        } else {
            progress.setWrongCount(progress.getWrongCount() + 1);
        }

        // Chỉ cập nhật tiến trình SRS nếu đã đến hạn ôn tập HOẶC là ôn tập trong ngày (heuristic)
        double elapsedDays = 0;
        if (progress.getLastReviewedAt() != null) {
            elapsedDays = java.time.temporal.ChronoUnit.SECONDS.between(progress.getLastReviewedAt(), now) / 86400.0;
        }

        if (!isDue && elapsedDays >= 0.01) {
            return;
        }

        int rating = fsrsService.calculateRating(isCorrect, responseTime);

        if (progress.getRepetitionCount() == 0) {
            fsrsService.initProgress(progress, rating);
            progress.setRepetitionCount(1);
        } else {
            fsrsService.updateProgress(progress, rating, now);
            progress.setRepetitionCount(progress.getRepetitionCount() + 1);
        }

        // Stability trong FSRS v5 là khoảng thời gian cho 90% khả năng ghi nhớ
        int interval = (int) Math.max(1, Math.round(progress.getStability()));
        progress.setIntervalDay(interval);
        progress.setNextReviewAt(now.plusDays(interval));
    }

    private void updateVocabularyStatus(UserVocabularyProgress progress, boolean isDue) {
        if (!isDue) {
            return;
        }

        if (progress.getRepetitionCount() > 0 && progress.getStatus() == VocabularyStatus.NOT_STARTED) {
            progress.setStatus(VocabularyStatus.LEARNING);
        }

        boolean hasLongTermMemory = progress.getStability() != null && progress.getStability() >= 30.0;
        
        if (hasLongTermMemory && progress.getStatus() != VocabularyStatus.MASTERED) {
            progress.setStatus(VocabularyStatus.MASTERED);
        }
        
        if (!hasLongTermMemory && progress.getStatus() == VocabularyStatus.MASTERED) {
            progress.setStatus(VocabularyStatus.LEARNING);
        }
    }

    private void checkAndUpgradeLevel(String userId) {
        long masteredCount = userVocabularyProgressRepository.countByUserIdAndStatus(userId, VocabularyStatus.MASTERED);
        if (masteredCount >= 100) {
            userLearningProfileRepository.findByUserId(userId).ifPresent(profile -> {
                com.tqt.englishApp.enums.Level currentLevel = profile.getLevel();
                com.tqt.englishApp.enums.Level nextLevel = currentLevel.getOffsetLevel(1);
                if (nextLevel != currentLevel) {
                    profile.setLevel(nextLevel);
                    userLearningProfileRepository.save(profile);
                }
            });
        }
    }

    public Page<UserVocabularyResponse> getUserVocabularyProgress(String userId, Map<String, String> params) {
        int page = Integer.parseInt(params.getOrDefault("page", "1")) - 1;
        int size = Integer.parseInt(params.getOrDefault("size", "10"));
        page = Math.max(0, page);
        Pageable pageable = PageRequest.of(page, size);

        Page<UserVocabularyProgress> progressPage = userVocabularyProgressRepository.findByUserId(userId, pageable);
        return progressPage.map(userVocabularyMapper::toUserVocabularyResponse);
    }
}
