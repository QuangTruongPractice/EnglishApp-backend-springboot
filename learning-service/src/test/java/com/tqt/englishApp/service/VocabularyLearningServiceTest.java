package com.tqt.englishApp.service;

import com.tqt.englishApp.dto.request.VocabularyProgressRequest;
import com.tqt.englishApp.dto.response.vocabulary.UserVocabularyResponse;
import com.tqt.englishApp.entity.UserLearningProfile;
import com.tqt.englishApp.entity.UserVocabularyProgress;
import com.tqt.englishApp.entity.VocabularyMeaning;
import com.tqt.englishApp.enums.Level;
import com.tqt.englishApp.enums.VocabularyStatus;
import com.tqt.englishApp.mapper.UserVocabularyMapper;
import com.tqt.englishApp.repository.UserLearningProfileRepository;
import com.tqt.englishApp.repository.UserVocabularyProgressRepository;
import com.tqt.englishApp.repository.VocabularyMeaningRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VocabularyLearningServiceTest {

    @InjectMocks
    private VocabularyLearningService service;

    @Mock
    private UserVocabularyProgressRepository userVocabularyProgressRepository;

    @Mock
    private VocabularyMeaningRepository VocabularyMeaningRepository;

    @Mock
    private UserLearningProfileRepository userLearningProfileRepository;

    @Mock
    private UserVocabularyMapper userVocabularyMapper;

    @Mock
    private FsrsService fsrsService;

    private VocabularyProgressRequest request;
    private VocabularyMeaning meaning;
    private UserVocabularyProgress existingProgress;
    private UserVocabularyResponse mockResponse;

    @BeforeEach
    void setUp() {
        meaning = new VocabularyMeaning();
        meaning.setId(1);

        request = new VocabularyProgressRequest();
        request.setUserId("user-1");
        request.setMeaningId(1);
        request.setIsCorrect(true);
        request.setResponseTime(2000L);

        existingProgress = UserVocabularyProgress.builder()
                .userId("user-1")
                .meaning(meaning)
                .repetitionCount(1)
                .correctCount(0)
                .wrongCount(0)
                .stability(5.0)
                .difficulty(4.0)
                .status(VocabularyStatus.LEARNING)
                .build();

        // UserVocabularyResponse uses @SuperBuilder — just create an empty instance via mock
        mockResponse = mock(UserVocabularyResponse.class);
    }

    // -----------------------------------------------------------------------
    // updateVocabularyProgress – existing progress record
    // -----------------------------------------------------------------------
    @Test
    void updateVocabularyProgress_ExistingRecord_CorrectAnswer_UpdatesProgress() {
        when(userVocabularyProgressRepository.findByUserIdAndMeaningId(anyString(), anyInt()))
                .thenReturn(Optional.of(existingProgress));
        when(userVocabularyProgressRepository.save(any())).thenReturn(existingProgress);
        when(userVocabularyMapper.toUserVocabularyResponse(any(UserVocabularyProgress.class)))
                .thenReturn(mockResponse);

        UserVocabularyResponse result = service.updateVocabularyProgress(request);

        assertNotNull(result);
        assertEquals(1, existingProgress.getCorrectCount());
        verify(userVocabularyProgressRepository).save(any());
        verify(fsrsService).calculateRating(true, 2000L);
    }

    @Test
    void updateVocabularyProgress_ExistingRecord_WrongAnswer_IncrementsWrongCount() {
        request.setIsCorrect(false);
        when(userVocabularyProgressRepository.findByUserIdAndMeaningId(anyString(), anyInt()))
                .thenReturn(Optional.of(existingProgress));
        when(userVocabularyProgressRepository.save(any())).thenReturn(existingProgress);
        when(userVocabularyMapper.toUserVocabularyResponse(any(UserVocabularyProgress.class)))
                .thenReturn(mockResponse);

        service.updateVocabularyProgress(request);

        assertEquals(1, existingProgress.getWrongCount());
        assertEquals(0, existingProgress.getCorrectCount());
    }

    @Test
    void updateVocabularyProgress_NewRecord_CreatedWithMeaning() {
        when(userVocabularyProgressRepository.findByUserIdAndMeaningId(anyString(), anyInt()))
                .thenReturn(Optional.empty());
        when(VocabularyMeaningRepository.findById(anyInt())).thenReturn(Optional.of(meaning));

        UserVocabularyProgress newProgress = UserVocabularyProgress.builder()
                .userId("user-1")
                .meaning(meaning)
                .repetitionCount(0)
                .correctCount(0)
                .wrongCount(0)
                .build();
        when(userVocabularyProgressRepository.save(any())).thenReturn(newProgress);
        when(userVocabularyMapper.toUserVocabularyResponse(any(UserVocabularyProgress.class)))
                .thenReturn(mockResponse);

        UserVocabularyResponse result = service.updateVocabularyProgress(request);

        assertNotNull(result);
        verify(VocabularyMeaningRepository).findById(1);
        verify(fsrsService).initProgress(any(), anyInt());
    }

    @Test
    void updateVocabularyProgress_NullIsCorrect_SkipsSrsUpdate() {
        request.setIsCorrect(null);
        when(userVocabularyProgressRepository.findByUserIdAndMeaningId(anyString(), anyInt()))
                .thenReturn(Optional.of(existingProgress));
        when(userVocabularyProgressRepository.save(any())).thenReturn(existingProgress);
        when(userVocabularyMapper.toUserVocabularyResponse(any(UserVocabularyProgress.class)))
                .thenReturn(mockResponse);

        service.updateVocabularyProgress(request);

        verify(fsrsService, never()).calculateRating(anyBoolean(), anyLong());
        verify(fsrsService, never()).updateProgress(any(), anyInt(), any());
    }

    @Test
    void updateVocabularyProgress_DueReview_CorrectAnswer_StatusBecomesLearning() {
        existingProgress.setRepetitionCount(0);
        existingProgress.setStatus(VocabularyStatus.NOT_STARTED);
        existingProgress.setNextReviewAt(LocalDateTime.now().minusDays(1));

        when(userVocabularyProgressRepository.findByUserIdAndMeaningId(anyString(), anyInt()))
                .thenReturn(Optional.of(existingProgress));
        when(userVocabularyProgressRepository.save(any())).thenReturn(existingProgress);
        when(userVocabularyMapper.toUserVocabularyResponse(any(UserVocabularyProgress.class)))
                .thenReturn(mockResponse);

        service.updateVocabularyProgress(request);

        assertEquals(VocabularyStatus.LEARNING, existingProgress.getStatus());
    }

    @Test
    void updateVocabularyProgress_Mastered_TriggersLevelCheck() {
        existingProgress.setStatus(VocabularyStatus.MASTERED);
        existingProgress.setStability(35.0);
        existingProgress.setNextReviewAt(LocalDateTime.now().minusDays(1));

        when(userVocabularyProgressRepository.findByUserIdAndMeaningId(anyString(), anyInt()))
                .thenReturn(Optional.of(existingProgress));
        when(userVocabularyProgressRepository.save(any())).thenReturn(existingProgress);
        when(userVocabularyMapper.toUserVocabularyResponse(any(UserVocabularyProgress.class)))
                .thenReturn(mockResponse);
        when(userVocabularyProgressRepository.countByUserIdAndStatus(anyString(), eq(VocabularyStatus.MASTERED)))
                .thenReturn(50L);

        service.updateVocabularyProgress(request);

        verify(userVocabularyProgressRepository).countByUserIdAndStatus("user-1", VocabularyStatus.MASTERED);
    }

    @Test
    void updateVocabularyProgress_HighStability_StatusBecomeMastered() {
        existingProgress.setRepetitionCount(5);
        existingProgress.setStatus(VocabularyStatus.LEARNING);
        existingProgress.setNextReviewAt(LocalDateTime.now().minusDays(1));
        doAnswer(inv -> {
            UserVocabularyProgress p = inv.getArgument(0);
            p.setStability(35.0);
            return null;
        }).when(fsrsService).updateProgress(any(), anyInt(), any());

        when(userVocabularyProgressRepository.findByUserIdAndMeaningId(anyString(), anyInt()))
                .thenReturn(Optional.of(existingProgress));
        when(userVocabularyProgressRepository.save(any())).thenReturn(existingProgress);
        when(userVocabularyMapper.toUserVocabularyResponse(any(UserVocabularyProgress.class)))
                .thenReturn(mockResponse);
        when(userVocabularyProgressRepository.countByUserIdAndStatus(anyString(), eq(VocabularyStatus.MASTERED)))
                .thenReturn(10L);

        service.updateVocabularyProgress(request);

        assertEquals(VocabularyStatus.MASTERED, existingProgress.getStatus());
    }

    @Test
    void updateVocabularyProgress_WasMastered_StabilityDropsBelow30_RevertToLearning() {
        existingProgress.setRepetitionCount(5);
        existingProgress.setStatus(VocabularyStatus.MASTERED);
        existingProgress.setStability(25.0);
        existingProgress.setNextReviewAt(LocalDateTime.now().minusDays(1));

        when(userVocabularyProgressRepository.findByUserIdAndMeaningId(anyString(), anyInt()))
                .thenReturn(Optional.of(existingProgress));
        when(userVocabularyProgressRepository.save(any())).thenReturn(existingProgress);
        when(userVocabularyMapper.toUserVocabularyResponse(any(UserVocabularyProgress.class)))
                .thenReturn(mockResponse);

        service.updateVocabularyProgress(request);

        assertEquals(VocabularyStatus.LEARNING, existingProgress.getStatus());
    }

    // -----------------------------------------------------------------------
    // getUserVocabularyProgress – pagination
    // -----------------------------------------------------------------------
    @Test
    void getUserVocabularyProgress_DefaultParams_ReturnsPage() {
        Map<String, String> params = new HashMap<>();
        Page<UserVocabularyProgress> progressPage = new PageImpl<>(List.of(existingProgress));
        when(userVocabularyProgressRepository.findByUserId(anyString(), any(Pageable.class)))
                .thenReturn(progressPage);
        when(userVocabularyMapper.toUserVocabularyResponse(any(UserVocabularyProgress.class)))
                .thenReturn(mockResponse);

        Page<UserVocabularyResponse> result = service.getUserVocabularyProgress("user-1", params);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void getUserVocabularyProgress_PageNegative_AdjustsToZero() {
        Map<String, String> params = new HashMap<>();
        params.put("page", "-3");

        Page<UserVocabularyProgress> progressPage = new PageImpl<>(List.of());
        when(userVocabularyProgressRepository.findByUserId(anyString(), any(Pageable.class)))
                .thenReturn(progressPage);

        Page<UserVocabularyResponse> result = service.getUserVocabularyProgress("user-1", params);

        assertNotNull(result);
    }

    @Test
    void updateVocabularyProgress_100MasteredWords_UpgradesLevel() {
        existingProgress.setStatus(VocabularyStatus.MASTERED);
        existingProgress.setStability(35.0);
        existingProgress.setNextReviewAt(LocalDateTime.now().minusDays(1));

        UserLearningProfile profile = new UserLearningProfile();
        profile.setUserId("user-1");
        profile.setLevel(Level.A1);

        when(userVocabularyProgressRepository.findByUserIdAndMeaningId(anyString(), anyInt()))
                .thenReturn(Optional.of(existingProgress));
        when(userVocabularyProgressRepository.save(any())).thenReturn(existingProgress);
        when(userVocabularyMapper.toUserVocabularyResponse(any(UserVocabularyProgress.class)))
                .thenReturn(mockResponse);
        when(userVocabularyProgressRepository.countByUserIdAndStatus(anyString(), eq(VocabularyStatus.MASTERED)))
                .thenReturn(100L);
        when(userLearningProfileRepository.findByUserId("user-1")).thenReturn(Optional.of(profile));
        when(userLearningProfileRepository.save(any())).thenReturn(profile);

        service.updateVocabularyProgress(request);

        assertEquals(Level.A2, profile.getLevel());
        verify(userLearningProfileRepository).save(profile);
    }
}
