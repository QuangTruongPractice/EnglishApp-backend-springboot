package com.tqt.englishApp.service;

import com.tqt.englishApp.entity.*;
import com.tqt.englishApp.enums.Level;
import com.tqt.englishApp.repository.UserVocabularyProgressRepository;
import com.tqt.englishApp.repository.VocabularyMeaningRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VocabularySelectionService {
    private final UserVocabularyProgressRepository progressRepository;
    private final VocabularyMeaningRepository meaningRepository;

    public List<VocabularyMeaning> selectMeaningsForSession(UserLearningProfile profile) {
        int targetMeanings = getTargetMeaningCount(profile.getDailyTarget());
        Set<Integer> selectedWordIds = new HashSet<>();
        List<VocabularyMeaning> finalMeanings = new ArrayList<>();

        // Bucket 1: Due SRS (Max 8)
        List<UserVocabularyProgress> dueProgress = progressRepository.findDueReviews(profile.getUserId(),
                LocalDateTime.now());
        for (UserVocabularyProgress p : dueProgress) {
            if (finalMeanings.size() >= 8)
                break;
            VocabularyMeaning meaning = p.getMeaning();
            if (!selectedWordIds.contains(meaning.getVocabulary().getId())) {
                finalMeanings.add(meaning);
                selectedWordIds.add(meaning.getVocabulary().getId());
            }
        }

        // Bucket 2: Weak meanings (Max 4)
        if (finalMeanings.size() < targetMeanings) {
            List<UserVocabularyProgress> weakProgress = progressRepository.findByUserId(profile.getUserId()).stream()
                    .filter(p -> p.getEaseFactor() < 1.8 && !finalMeanings.contains(p.getMeaning()))
                    .limit(20) // Limit search
                    .collect(Collectors.toList());

            for (UserVocabularyProgress p : weakProgress) {
                if (finalMeanings.size() >= (8 + 4) || finalMeanings.size() >= targetMeanings)
                    break;
                VocabularyMeaning meaning = p.getMeaning();
                if (!selectedWordIds.contains(meaning.getVocabulary().getId())) {
                    finalMeanings.add(meaning);
                    selectedWordIds.add(meaning.getVocabulary().getId());
                }
            }
        }

        // Bucket 3: New Meanings
        if (finalMeanings.size() < targetMeanings) {
            List<Level> levels = getLevelsUpTo(profile.getLevel());
            
            // Step 3a: Search meanings matching user's LearningGoal
            List<VocabularyMeaning> goalSpecificMeanings = meaningRepository.findNewMeanings(profile.getUserId(),
                    profile.getGoal(), levels);

            for (VocabularyMeaning meaning : goalSpecificMeanings) {
                if (finalMeanings.size() >= targetMeanings)
                    break;
                if (!selectedWordIds.contains(meaning.getVocabulary().getId())) {
                    finalMeanings.add(meaning);
                    selectedWordIds.add(meaning.getVocabulary().getId());
                }
            }

            // Step 3b: Fallback if not enough goal-specific meanings found
            if (finalMeanings.size() < targetMeanings) {
                List<VocabularyMeaning> allNewMeanings = meaningRepository.findAllNewMeanings(profile.getUserId(), levels);
                for (VocabularyMeaning meaning : allNewMeanings) {
                    if (finalMeanings.size() >= targetMeanings)
                        break;
                    if (!selectedWordIds.contains(meaning.getVocabulary().getId())) {
                        finalMeanings.add(meaning);
                        selectedWordIds.add(meaning.getVocabulary().getId());
                    }
                }
            }
        }

        return finalMeanings;
    }

    private int getTargetMeaningCount(int dailyTargetMinutes) {
        if (dailyTargetMinutes <= 5)
            return 6;
        if (dailyTargetMinutes <= 15)
            return 15;
        return 25;
    }

    private List<Level> getLevelsUpTo(Level level) {
        List<Level> levels = new ArrayList<>();
        for (Level l : Level.values()) {
            levels.add(l);
            if (l == level)
                break;
        }
        return levels;
    }
}
