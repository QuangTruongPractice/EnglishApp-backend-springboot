package com.tqt.englishApp.service;

import com.tqt.englishApp.dto.response.DailyVocabularyItem;
import com.tqt.englishApp.entity.*;
import com.tqt.englishApp.enums.Level;
import com.tqt.englishApp.enums.VocabularyStatus;
import com.tqt.englishApp.repository.MainTopicRepository;
import com.tqt.englishApp.repository.UserLearningProfileRepository;
import com.tqt.englishApp.repository.UserVocabularyProgressRepository;
import com.tqt.englishApp.repository.VocabularyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VocabularySelectionService {

    @Autowired
    private UserLearningProfileRepository userLearningProfileRepository;

    @Autowired
    private UserVocabularyProgressRepository userVocabularyProgressRepository;

    @Autowired
    private VocabularyRepository vocabularyRepository;

    @Autowired
    private MainTopicRepository mainTopicRepository;

    /**
     * Lấy danh sách từ vựng hằng ngày cho người dùng.
     * Quy trình:
     * 1. Lấy tất cả Vocabulary có meaning đến hạn review.
     * 2. Nếu đủ chỉ tiêu hằng ngày -> trả về.
     * 3. Nếu thiếu -> lấy thêm Vocabulary mới theo tỉ lệ Level.
     */
    public List<DailyVocabularyItem> getDailyVocabulary(String userId) {
        UserLearningProfile profile = userLearningProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User profile not found"));

        int dailyTarget = profile.getDailyTarget();

        // Lấy trực tiếp Vocabulary từ review
        List<Vocabulary> reviewVocabs = getDueReviewVocabularies(userId);

        // Batch query status cho tất cả review vocabs 1 lần
        Map<Integer, VocabularyStatus> statusMap = batchCalculateStatus(userId, reviewVocabs);

        List<DailyVocabularyItem> result = new ArrayList<>(reviewVocabs.stream()
                .map(v -> new DailyVocabularyItem(v, true,
                        statusMap.getOrDefault(v.getId(), VocabularyStatus.LEARNING)))
                .collect(Collectors.toList()));

        if (result.size() < dailyTarget) {
            int neededNew = dailyTarget - result.size();
            Set<Integer> reviewVocabIds = reviewVocabs.stream()
                    .map(Vocabulary::getId)
                    .collect(Collectors.toSet());

            // Fetch trực tiếp Vocabulary, count chính xác
            List<Vocabulary> newVocabs = getNewVocabulariesForDailyTarget(profile, neededNew)
                    .stream()
                    .filter(v -> !reviewVocabIds.contains(v.getId()))
                    .collect(Collectors.toList());

            newVocabs.stream()
                    .map(v -> new DailyVocabularyItem(v, false, VocabularyStatus.NOT_STARTED))
                    .forEach(result::add);
        }

        return result.size() > dailyTarget ? result.subList(0, dailyTarget) : result;
    }

    /**
     * Lấy Vocabulary có ít nhất 1 meaning đến hạn review.
     */
    private List<Vocabulary> getDueReviewVocabularies(String userId) {
        List<UserVocabularyProgress> dueProgressList = userVocabularyProgressRepository
                .findDueReviews(userId, LocalDateTime.now());
        return dueProgressList.stream()
                .map(p -> p.getMeaning().getVocabulary())
                .distinct()
                .collect(Collectors.toList());
    }

    private Map<Integer, VocabularyStatus> batchCalculateStatus(String userId, List<Vocabulary> vocabularies) {
        if (vocabularies.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Integer> vocabIds = vocabularies.stream()
                .map(Vocabulary::getId)
                .collect(Collectors.toList());

        List<UserVocabularyProgress> allProgress = userVocabularyProgressRepository
                .findByUserIdAndMeaning_Vocabulary_IdIn(userId, vocabIds);

        return allProgress.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getMeaning().getVocabulary().getId(),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> list.stream()
                                        .allMatch(p -> p.getStatus() == VocabularyStatus.MASTERED)
                                                ? VocabularyStatus.MASTERED
                                                : VocabularyStatus.LEARNING)));
    }

    /**
     * Fetch trực tiếp Vocabulary từ VocabularyRepository, không qua WordMeaning.
     */
    private List<Vocabulary> getNewVocabulariesForDailyTarget(UserLearningProfile profile, int needed) {
        Level currentLevel = profile.getLevel();
        LevelDistribution distribution = calculateLevelDistribution(currentLevel, needed);

        List<Vocabulary> newVocabs = new ArrayList<>();

        newVocabs.addAll(fetchVocabulariesWithTopicFallback(profile, currentLevel, distribution.targetCount));

        if (distribution.lowerCount > 0) {
            newVocabs.addAll(
                    fetchVocabulariesWithTopicFallback(profile, currentLevel.getOffsetLevel(-1),
                            distribution.lowerCount));
        }

        if (distribution.higherCount > 0) {
            newVocabs.addAll(
                    fetchVocabulariesWithTopicFallback(profile, currentLevel.getOffsetLevel(1),
                            distribution.higherCount));
        }

        return newVocabs;
    }

    private LevelDistribution calculateLevelDistribution(Level currentLevel, int needed) {
        int target = (int) Math.ceil(needed * 0.6);
        int lower = (int) Math.floor(needed * 0.2);
        int higher = (int) Math.floor(needed * 0.2);

        if (currentLevel == Level.A1) {
            target += lower;
            lower = 0;
        } else if (currentLevel == Level.C2) {
            target += higher;
            higher = 0;
        }

        int total = target + lower + higher;
        if (total < needed) {
            target += (needed - total);
        }

        return new LevelDistribution(target, lower, higher);
    }

    /**
     * Fetch Vocabulary trực tiếp từ VocabularyRepository.
     * Luôn cập nhật currentTopicId khi chuyển sang topic mới.
     */
    private List<Vocabulary> fetchVocabulariesWithTopicFallback(UserLearningProfile profile, Level level, int count) {
        if (count <= 0)
            return Collections.emptyList();

        List<MainTopic> topicPath = mainTopicRepository.findByGoalOrderByTopicOrderAsc(profile.getGoal());
        if (topicPath.isEmpty())
            return Collections.emptyList();

        ensureCurrentTopicIsSet(profile, topicPath);

        List<Vocabulary> gathered = new ArrayList<>();
        int currentTopicIdx = findTopicIndex(topicPath, profile.getCurrentTopicId());
        int lastUsedTopicIdx = currentTopicIdx;

        for (int i = currentTopicIdx; i < topicPath.size() && gathered.size() < count; i++) {
            MainTopic topic = topicPath.get(i);
            int stillNeeded = count - gathered.size();

            List<Vocabulary> fromTopic = vocabularyRepository.findNewVocabulariesByLevelAndTopic(
                    profile.getUserId(), level, topic.getId(), PageRequest.of(0, stillNeeded));

            gathered.addAll(fromTopic);

            if (!fromTopic.isEmpty()) {
                lastUsedTopicIdx = i;
            }

            // Nếu hết từ mới trong topic hiện tại và chưa đủ, tiếp tục vòng lặp
        }

        // Cập nhật topic sang topic cuối cùng có dữ liệu
        if (lastUsedTopicIdx != currentTopicIdx) {
            moveToNextTopic(profile, topicPath.get(lastUsedTopicIdx));
        }

        return gathered;
    }

    private void ensureCurrentTopicIsSet(UserLearningProfile profile, List<MainTopic> topicPath) {
        if (profile.getCurrentTopicId() == null && !topicPath.isEmpty()) {
            profile.setCurrentTopicId(topicPath.get(0).getId());
            userLearningProfileRepository.save(profile);
        }
    }

    private int findTopicIndex(List<MainTopic> topicPath, Integer topicId) {
        for (int i = 0; i < topicPath.size(); i++) {
            if (topicPath.get(i).getId().equals(topicId))
                return i;
        }
        return 0;
    }

    private void moveToNextTopic(UserLearningProfile profile, MainTopic nextTopic) {
        profile.setCurrentTopicId(nextTopic.getId());
        userLearningProfileRepository.save(profile);
    }

    private static class LevelDistribution {
        final int targetCount;
        final int lowerCount;
        final int higherCount;

        LevelDistribution(int target, int lower, int higher) {
            this.targetCount = target;
            this.lowerCount = lower;
            this.higherCount = higher;
        }
    }
}
