package com.tqt.englishApp.service;

import com.tqt.englishApp.dto.request.VideoProgressRequest;
import com.tqt.englishApp.dto.request.VocabularyProgressRequest;
import com.tqt.englishApp.dto.response.LeaderBoardResponse;
import com.tqt.englishApp.dto.response.LeaderBoardWrapperResponse;
import com.tqt.englishApp.entity.*;
import com.tqt.englishApp.mapper.UserVideoMapper;
import com.tqt.englishApp.mapper.UserVocabularyMapper;
import com.tqt.englishApp.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LearningProgressServiceTest {

    @InjectMocks
    private LearningProgressService learningProgressService;

    @Mock
    private UserVocabularyProgressRepository userVocabularyProgressRepository;

    @Mock
    private UserVideoProgressRepository userVideoProgressRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private VocabularyRepository vocabularyRepository;

    @Mock
    private VideoRepository videoRepository;

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private UserQuizProgressRepository userQuizProgressRepository;

    @Mock
    private UserVideoMapper userVideoMapper;

    @Mock
    private UserVocabularyMapper userVocabularyMapper;

    private User user;
    private Vocabulary vocabulary;
    private Video video;
    private Quiz quiz;
    private UserVocabularyProgress vocabularyProgress;
    private UserVideoProgress videoProgress;
    private UserQuizProgress quizProgress;

    @BeforeEach
    void init() {
        user = User.builder().id("user123").username("testuser").build();
        vocabulary = Vocabulary.builder().id(1).word("Hello").build();
        video = Video.builder().id(1).title("Sample Video").build();
        quiz = Quiz.builder().id(1).question("Test Quiz").build();

        vocabularyProgress = UserVocabularyProgress.builder()
                .user(user)
                .vocabulary(vocabulary)
                .viewedFlashcard(false)
                .practicedPronunciation(false)
                .build();

        videoProgress = UserVideoProgress.builder()
                .user(user)
                .video(video)
                .watchedDuration(0)
                .progressPercentage(0.0)
                .build();

        quizProgress = UserQuizProgress.builder()
                .user(user)
                .quiz(quiz)
                .count(0)
                .build();
    }

    // --- Vocabulary Progress ---

    @Test
    void updateVocabularyProgress_Existing_Success() {
        VocabularyProgressRequest request = new VocabularyProgressRequest("user123", 1, true, true);
        when(userVocabularyProgressRepository.findByUserIdAndVocabularyId("user123", 1))
                .thenReturn(Optional.of(vocabularyProgress));
        when(userVocabularyProgressRepository.save(any())).thenReturn(vocabularyProgress);

        UserVocabularyProgress result = learningProgressService.updateVocabularyProgress(request);

        assertNotNull(result);
        assertTrue(result.getViewedFlashcard());
        assertTrue(result.getPracticedPronunciation());
        verify(userVocabularyProgressRepository).save(vocabularyProgress);
    }

    @Test
    void updateVocabularyProgress_New_Success() {
        VocabularyProgressRequest request = new VocabularyProgressRequest("user123", 1, true, false);
        when(userVocabularyProgressRepository.findByUserIdAndVocabularyId(anyString(), anyInt()))
                .thenReturn(Optional.empty());
        when(userRepository.findById("user123")).thenReturn(Optional.of(user));
        when(vocabularyRepository.findById(1)).thenReturn(Optional.of(vocabulary));
        when(userVocabularyProgressRepository.save(any())).thenReturn(vocabularyProgress);

        UserVocabularyProgress result = learningProgressService.updateVocabularyProgress(request);

        assertNotNull(result);
        verify(userRepository).findById("user123");
        verify(vocabularyRepository).findById(1);
    }

    @Test
    void updateVocabularyProgress_NullFields_Success() {
        VocabularyProgressRequest request = VocabularyProgressRequest.builder()
                .userId("user123")
                .vocabularyId(1)
                .viewedFlashcard(null)
                .practicedPronunciation(null)
                .build();
        when(userVocabularyProgressRepository.findByUserIdAndVocabularyId(anyString(), anyInt()))
                .thenReturn(Optional.of(vocabularyProgress));
        when(userVocabularyProgressRepository.save(any())).thenReturn(vocabularyProgress);

        UserVocabularyProgress result = learningProgressService.updateVocabularyProgress(request);

        assertNotNull(result);
        assertFalse(result.getViewedFlashcard()); // Default false in service logic
        assertFalse(result.getPracticedPronunciation());
    }

    // --- Video Progress ---

    @Test
    void updateVideoProgress_Existing_Success() {
        VideoProgressRequest request = new VideoProgressRequest("user123", 1, 60, 120, 200);
        when(userVideoProgressRepository.findByUserIdAndVideoId("user123", 1))
                .thenReturn(Optional.of(videoProgress));
        when(userVideoProgressRepository.save(any())).thenReturn(videoProgress);

        UserVideoProgress result = learningProgressService.updateVideoProgress(request);

        assertNotNull(result);
        assertEquals(60, result.getWatchedDuration());
        assertEquals(120, result.getLastPosition());
        assertEquals(30.0, result.getProgressPercentage()); // 60/200 * 100
    }

    @Test
    void updateVideoProgress_New_Success() {
        VideoProgressRequest request = new VideoProgressRequest("user123", 1, 50, 50, 100);
        when(userVideoProgressRepository.findByUserIdAndVideoId(anyString(), anyInt()))
                .thenReturn(Optional.empty());
        when(userRepository.findById("user123")).thenReturn(Optional.of(user));
        when(videoRepository.findById(1)).thenReturn(Optional.of(video));
        when(userVideoProgressRepository.save(any())).thenReturn(videoProgress);

        UserVideoProgress result = learningProgressService.updateVideoProgress(request);

        assertNotNull(result);
        verify(userVideoProgressRepository).save(any());
    }

    @Test
    void updateVideoProgress_DurationZero_NoPercentageCalculation() {
        VideoProgressRequest request = new VideoProgressRequest("user123", 1, 50, 50, 0);
        when(userVideoProgressRepository.findByUserIdAndVideoId(anyString(), anyInt()))
                .thenReturn(Optional.of(videoProgress));
        when(userVideoProgressRepository.save(any())).thenReturn(videoProgress);

        UserVideoProgress result = learningProgressService.updateVideoProgress(request);

        assertEquals(0.0, result.getProgressPercentage());
    }

    // --- Get Progress ---

    @Test
    void getUserVocabularyProgress_Success() {
        List<UserVocabularyProgress> list = List.of(vocabularyProgress);
        when(userVocabularyProgressRepository.findByUserId("user123")).thenReturn(list);

        learningProgressService.getUserVocabularyProgress("user123");

        verify(userVocabularyMapper).toUserVocabularyResponse(list);
    }

    @Test
    void getUserVideoProgress_Success() {
        List<UserVideoProgress> list = List.of(videoProgress);
        when(userVideoProgressRepository.findByUserId("user123")).thenReturn(list);

        learningProgressService.getUserVideoProgress("user123");

        verify(userVideoMapper).toUserVideoResponse(list);
    }

    // --- Leaderboard ---

    @Test
    void getLeaderBoardWithCurrentUser_Success() {
        LeaderBoardResponse item1 = LeaderBoardResponse.builder().userId("user1").build();
        LeaderBoardResponse item2 = LeaderBoardResponse.builder().userId("user123").build();
        List<LeaderBoardResponse> raw = List.of(item1, item2);

        when(userVocabularyProgressRepository.getUserRanking()).thenReturn(raw);

        LeaderBoardWrapperResponse result = learningProgressService.getLeaderBoardWithCurrentUser("user123");

        assertNotNull(result);
        assertEquals(2, result.getLeaderBoard().size());
        assertEquals("user123", result.getCurrentUser().getUserId());
    }

    @Test
    void getLeaderBoardWithCurrentUser_NotFound() {
        LeaderBoardResponse item1 = LeaderBoardResponse.builder().userId("user1").build();
        List<LeaderBoardResponse> raw = List.of(item1);

        when(userVocabularyProgressRepository.getUserRanking()).thenReturn(raw);

        LeaderBoardWrapperResponse result = learningProgressService.getLeaderBoardWithCurrentUser("otherUser");

        assertNotNull(result);
        assertNull(result.getCurrentUser());
    }

    // --- Quiz Progress ---

    @Test
    void updateProgress_Quiz_New() {
        when(userQuizProgressRepository.findByUserIdAndQuizId("user123", 1)).thenReturn(Optional.empty());
        when(userRepository.findById("user123")).thenReturn(Optional.of(user));
        when(quizRepository.findById(1)).thenReturn(Optional.of(quiz));
        when(userQuizProgressRepository.save(any())).thenReturn(quizProgress);

        UserQuizProgress result = learningProgressService.updateProgress("user123", 1);

        assertNotNull(result);
        verify(userQuizProgressRepository).save(any());
    }

    @Test
    void updateProgress_Quiz_Existing() {
        when(userQuizProgressRepository.findByUserIdAndQuizId("user123", 1)).thenReturn(Optional.of(quizProgress));
        when(userQuizProgressRepository.save(any())).thenReturn(quizProgress);

        UserQuizProgress result = learningProgressService.updateProgress("user123", 1);

        assertEquals(1, quizProgress.getCount());
        verify(userQuizProgressRepository).save(quizProgress);
    }
}
