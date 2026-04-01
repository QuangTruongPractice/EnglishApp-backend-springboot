package com.tqt.englishApp.service;

import com.tqt.englishApp.dto.request.VocabularyProgressRequest;
import com.tqt.englishApp.dto.response.vocabulary.UserVocabularyResponse;
import com.tqt.englishApp.entity.UserVocabularyProgress;
import com.tqt.englishApp.enums.VocabularyStatus;
import com.tqt.englishApp.mapper.UserVocabularyMapper;
import com.tqt.englishApp.mapper.VocabularyMapper;
import com.tqt.englishApp.mapper.VocabularyMeaningMapper;
import com.tqt.englishApp.repository.UserLearningProfileRepository;
import com.tqt.englishApp.repository.UserVocabularyProgressRepository;
import com.tqt.englishApp.repository.VocabularyMeaningRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
    private VocabularyMapper vocabularyMapper;

    @Autowired
    private VocabularyMeaningMapper VocabularyMeaningMapper;

    public UserVocabularyProgress updateVocabularyProgress(VocabularyProgressRequest request) {
        UserVocabularyProgress progress = userVocabularyProgressRepository
                .findByUserIdAndMeaningId(request.getUserId(), request.getMeaningId())
                .orElse(UserVocabularyProgress.builder()
                        .userId(request.getUserId())
                        .meaning(VocabularyMeaningRepository.findById(request.getMeaningId()).orElse(null))
                        .build());

        LocalDateTime now = LocalDateTime.now();

        if (request.getIsCorrect() != null) {
            updateSrsData(progress, request.getIsCorrect(), now);
        }

        updateVocabularyStatus(progress);

        progress.setUpdatedAt(now);
        UserVocabularyProgress savedProgress = userVocabularyProgressRepository.save(progress);

        if (progress.getStatus() == VocabularyStatus.MASTERED) {
            checkAndUpgradeLevel(request.getUserId());
        }

        return savedProgress;
    }

    private void updateSrsData(UserVocabularyProgress progress, boolean isCorrect, LocalDateTime now) {
        if (isCorrect) {
            progress.setRepetitionCount(progress.getRepetitionCount() + 1);
            progress.setCorrectCount(progress.getCorrectCount() + 1);

            // interval = interval * easeFactor
            progress.setIntervalDay((int) (progress.getIntervalDay() * progress.getEaseFactor()));
            progress.setNextReviewAt(now.plusDays(progress.getIntervalDay()));
        } else {
            progress.setRepetitionCount(0);
            progress.setWrongCount(progress.getWrongCount() + 1);
            progress.setIntervalDay(1);
            progress.setNextReviewAt(now);
        }
    }

    private void updateVocabularyStatus(UserVocabularyProgress progress) {
        if (progress.getRepetitionCount() > 0 && progress.getStatus() == VocabularyStatus.NOT_STARTED) {
            progress.setStatus(VocabularyStatus.LEARNING);
        }

        if (progress.getRepetitionCount() >= 5 && progress.getStatus() != VocabularyStatus.MASTERED) {
            progress.setStatus(VocabularyStatus.MASTERED);
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

    public List<UserVocabularyResponse> getUserVocabularyProgress(String userId) {
        List<UserVocabularyProgress> progressList = userVocabularyProgressRepository.findByUserId(userId);
        return userVocabularyMapper.toUserVocabularyResponse(progressList);
    }
}
