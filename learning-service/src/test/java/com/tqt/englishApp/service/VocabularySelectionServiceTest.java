package com.tqt.englishApp.service;

import com.tqt.englishApp.entity.*;
import com.tqt.englishApp.enums.Level;
import com.tqt.englishApp.enums.LearningGoal;
import com.tqt.englishApp.repository.UserVocabularyProgressRepository;
import com.tqt.englishApp.repository.VocabularyMeaningRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VocabularySelectionServiceTest {

    @InjectMocks
    private VocabularySelectionService service;

    @Mock
    private UserVocabularyProgressRepository progressRepository;

    @Mock
    private VocabularyMeaningRepository meaningRepository;

    private UserLearningProfile profile;

    @BeforeEach
    void setUp() {
        profile = new UserLearningProfile();
        profile.setUserId("user-1");
        profile.setLevel(Level.A2);
        profile.setDailyTarget(15); // → targetMeanings = 15
        profile.setGoal(LearningGoal.TRAVEL);
    }

    // -----------------------------------------------------------------------
    // Helper to build VocabularyMeaning with a Vocabulary
    // -----------------------------------------------------------------------
    private VocabularyMeaning buildMeaning(int id, int vocabId) {
        Vocabulary vocab = new Vocabulary();
        vocab.setId(vocabId);

        List<SubTopic> subTopics = new ArrayList<>();
        vocab.setSubTopics(subTopics);

        VocabularyMeaning meaning = new VocabularyMeaning();
        meaning.setId(id);
        meaning.setVocabulary(vocab);
        return meaning;
    }

    private UserVocabularyProgress buildProgress(VocabularyMeaning meaning, double difficulty) {
        return UserVocabularyProgress.builder()
                .meaning(meaning)
                .difficulty(difficulty)
                .nextReviewAt(LocalDateTime.now().minusDays(1))
                .build();
    }

    // -----------------------------------------------------------------------
    // selectMeaningsForSession
    // -----------------------------------------------------------------------
    @Test
    void selectMeanings_DueReviewsFillBucket1_UpTo8() {
        List<UserVocabularyProgress> dueList = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            dueList.add(buildProgress(buildMeaning(i, i), 4.0));
        }
        when(progressRepository.findDueReviews(anyString(), any(LocalDateTime.class))).thenReturn(dueList);
        when(progressRepository.findByUserId(anyString())).thenReturn(new ArrayList<>());
        when(meaningRepository.findNewMeanings(anyString(), any(), anyList())).thenReturn(new ArrayList<>());
        when(meaningRepository.findAllNewMeanings(anyString(), anyList())).thenReturn(new ArrayList<>());

        List<VocabularyMeaning> result = service.selectMeaningsForSession(profile);

        // Bucket 1 giới hạn 8, nhưng có thể ít hơn nếu vocab trùng
        assertTrue(result.size() <= 8);
    }

    @Test
    void selectMeanings_NoDueReviews_FillsWithWeakWords() {
        when(progressRepository.findDueReviews(anyString(), any())).thenReturn(new ArrayList<>());

        List<UserVocabularyProgress> allProgress = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            allProgress.add(buildProgress(buildMeaning(i, i), 9.5)); // difficulty > 8.0
        }
        when(progressRepository.findByUserId(anyString())).thenReturn(allProgress);
        when(meaningRepository.findNewMeanings(anyString(), any(), anyList())).thenReturn(new ArrayList<>());
        when(meaningRepository.findAllNewMeanings(anyString(), anyList())).thenReturn(new ArrayList<>());

        List<VocabularyMeaning> result = service.selectMeaningsForSession(profile);

        assertTrue(result.size() <= 5);
        verify(progressRepository).findByUserId("user-1");
    }

    @Test
    void selectMeanings_FillsRemainingWithNewWords() {
        when(progressRepository.findDueReviews(anyString(), any())).thenReturn(new ArrayList<>());
        when(progressRepository.findByUserId(anyString())).thenReturn(new ArrayList<>());

        List<VocabularyMeaning> newMeanings = new ArrayList<>();
        for (int i = 1; i <= 15; i++) {
            newMeanings.add(buildMeaning(i, i));
        }
        when(meaningRepository.findNewMeanings(anyString(), any(), anyList())).thenReturn(newMeanings);

        List<VocabularyMeaning> result = service.selectMeaningsForSession(profile);

        assertEquals(15, result.size()); // dailyTarget=15 → targetMeanings=15
    }

    @Test
    void selectMeanings_FallbackToAllNewMeanings_WhenGoalSpecificNotEnough() {
        when(progressRepository.findDueReviews(anyString(), any())).thenReturn(new ArrayList<>());
        when(progressRepository.findByUserId(anyString())).thenReturn(new ArrayList<>());
        when(meaningRepository.findNewMeanings(anyString(), any(), anyList())).thenReturn(new ArrayList<>());

        List<VocabularyMeaning> allNew = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            allNew.add(buildMeaning(i, i));
        }
        when(meaningRepository.findAllNewMeanings(anyString(), anyList())).thenReturn(allNew);

        List<VocabularyMeaning> result = service.selectMeaningsForSession(profile);

        assertEquals(10, result.size());
        verify(meaningRepository).findAllNewMeanings(anyString(), anyList());
    }

    @Test
    void selectMeanings_LowTarget5Min_Returns6Meanings() {
        profile.setDailyTarget(5); // → targetMeanings = 6

        when(progressRepository.findDueReviews(anyString(), any())).thenReturn(new ArrayList<>());
        when(progressRepository.findByUserId(anyString())).thenReturn(new ArrayList<>());

        List<VocabularyMeaning> newMeanings = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            newMeanings.add(buildMeaning(i, i));
        }
        when(meaningRepository.findNewMeanings(anyString(), any(), anyList())).thenReturn(newMeanings);

        List<VocabularyMeaning> result = service.selectMeaningsForSession(profile);

        assertEquals(6, result.size());
    }

    @Test
    void selectMeanings_HighTarget30Min_Returns25Meanings() {
        profile.setDailyTarget(30); // → targetMeanings = 25

        when(progressRepository.findDueReviews(anyString(), any())).thenReturn(new ArrayList<>());
        when(progressRepository.findByUserId(anyString())).thenReturn(new ArrayList<>());

        List<VocabularyMeaning> newMeanings = new ArrayList<>();
        for (int i = 1; i <= 30; i++) {
            newMeanings.add(buildMeaning(i, i));
        }
        when(meaningRepository.findNewMeanings(anyString(), any(), anyList())).thenReturn(newMeanings);

        List<VocabularyMeaning> result = service.selectMeaningsForSession(profile);

        assertEquals(25, result.size());
    }

    @Test
    void selectMeanings_DeduplicatesWordsByVocabId() {
        // Hai meanings cùng vocab ID → chỉ lấy 1
        VocabularyMeaning m1 = buildMeaning(1, 100);
        VocabularyMeaning m2 = buildMeaning(2, 100); // same vocabId!

        List<UserVocabularyProgress> dueList = List.of(
                buildProgress(m1, 4.0),
                buildProgress(m2, 4.0)
        );
        when(progressRepository.findDueReviews(anyString(), any())).thenReturn(dueList);
        when(progressRepository.findByUserId(anyString())).thenReturn(new ArrayList<>());
        when(meaningRepository.findNewMeanings(anyString(), any(), anyList())).thenReturn(new ArrayList<>());
        when(meaningRepository.findAllNewMeanings(anyString(), anyList())).thenReturn(new ArrayList<>());

        List<VocabularyMeaning> result = service.selectMeaningsForSession(profile);

        assertEquals(1, result.size(), "Duplicate vocab IDs should be deduplicated");
    }

    @Test
    void selectMeanings_LevelsUpToUserLevel_AreIncluded() {
        profile.setLevel(Level.B1);

        when(progressRepository.findDueReviews(anyString(), any())).thenReturn(new ArrayList<>());
        when(progressRepository.findByUserId(anyString())).thenReturn(new ArrayList<>());
        when(meaningRepository.findNewMeanings(anyString(), any(), anyList())).thenReturn(new ArrayList<>());
        when(meaningRepository.findAllNewMeanings(anyString(), anyList())).thenReturn(new ArrayList<>());

        service.selectMeaningsForSession(profile);

        // Xác nhận levels truyền vào bao gồm A1, A2, B1 (không có B2, C1, C2)
        verify(meaningRepository).findNewMeanings(eq("user-1"), eq(LearningGoal.TRAVEL),
                argThat(levels -> levels.contains(Level.A1)
                        && levels.contains(Level.A2)
                        && levels.contains(Level.B1)
                        && !levels.contains(Level.B2)));
    }
}
