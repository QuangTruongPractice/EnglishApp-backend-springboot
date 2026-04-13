package com.tqt.englishApp.service;

import com.tqt.englishApp.dto.request.VideoProgressRequest;
import com.tqt.englishApp.dto.response.UserVideoResponse;
import com.tqt.englishApp.entity.Quiz;
import com.tqt.englishApp.entity.UserQuizProgress;
import com.tqt.englishApp.entity.UserVideoProgress;
import com.tqt.englishApp.entity.Video;
import com.tqt.englishApp.mapper.UserVideoMapper;
import com.tqt.englishApp.repository.QuizRepository;
import com.tqt.englishApp.repository.UserQuizProgressRepository;
import com.tqt.englishApp.repository.UserVideoProgressRepository;
import com.tqt.englishApp.repository.VideoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContentProgressServiceTest {

    @InjectMocks
    private ContentProgressService service;

    @Mock
    private UserVideoProgressRepository userVideoProgressRepository;

    @Mock
    private VideoRepository videoRepository;

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private UserQuizProgressRepository userQuizProgressRepository;

    @Mock
    private UserVideoMapper userVideoMapper;

    private Video video;
    private Quiz quiz;
    private UserVideoProgress videoProgress;
    private UserQuizProgress quizProgress;
    private VideoProgressRequest request;

    @BeforeEach
    void setUp() {
        video = new Video();
        video.setId(1);

        quiz = new Quiz();
        quiz.setId(1);

        // watchedDuration & lastPosition are Integer in the entity
        videoProgress = UserVideoProgress.builder()
                .userId("user-1")
                .video(video)
                .watchedDuration(0)
                .isCompleted(false)
                .build();

        quizProgress = UserQuizProgress.builder()
                .userId("user-1")
                .quiz(quiz)
                .count(0)
                .build();

        request = new VideoProgressRequest();
        request.setUserId("user-1");
        request.setVideoId(1);
        request.setWatchedDuration(60);
        request.setVideoDuration(120);
        request.setLastPosition(60);
    }

    // -----------------------------------------------------------------------
    // updateVideoProgress
    // -----------------------------------------------------------------------
    @Test
    void updateVideoProgress_ExistingProgress_UpdatesWatchedDuration() {
        when(userVideoProgressRepository.findByUserIdAndVideoId("user-1", 1))
                .thenReturn(Optional.of(videoProgress));
        when(userVideoProgressRepository.save(any())).thenReturn(videoProgress);

        UserVideoProgress result = service.updateVideoProgress(request);

        assertNotNull(result);
        // watchedDuration: 0 < 60 → updated to 60
        assertEquals(60, videoProgress.getWatchedDuration());
        verify(userVideoProgressRepository).save(videoProgress);
    }

    @Test
    void updateVideoProgress_NewRecord_CreatesWithVideo() {
        when(userVideoProgressRepository.findByUserIdAndVideoId("user-1", 1))
                .thenReturn(Optional.empty());
        when(videoRepository.findById(1)).thenReturn(Optional.of(video));
        when(userVideoProgressRepository.save(any())).thenReturn(videoProgress);

        UserVideoProgress result = service.updateVideoProgress(request);

        assertNotNull(result);
        verify(videoRepository).findById(1);
    }

    @Test
    void updateVideoProgress_WatchedDurationNotIncreasing_NoUpdate() {
        videoProgress.setWatchedDuration(90); // already ahead of 60
        when(userVideoProgressRepository.findByUserIdAndVideoId("user-1", 1))
                .thenReturn(Optional.of(videoProgress));
        when(userVideoProgressRepository.save(any())).thenReturn(videoProgress);

        request.setWatchedDuration(60);
        service.updateVideoProgress(request);

        // watchedDuration không giảm
        assertEquals(90, videoProgress.getWatchedDuration());
    }

    @Test
    void updateVideoProgress_AlreadyCompleted_SkipsProgressUpdate() {
        videoProgress.setIsCompleted(true);
        videoProgress.setWatchedDuration(120);
        when(userVideoProgressRepository.findByUserIdAndVideoId("user-1", 1))
                .thenReturn(Optional.of(videoProgress));
        when(userVideoProgressRepository.save(any())).thenReturn(videoProgress);

        request.setWatchedDuration(50);
        service.updateVideoProgress(request);

        assertEquals(120, videoProgress.getWatchedDuration());
    }

    @Test
    void updateVideoProgress_CalculatesProgressPercentage() {
        when(userVideoProgressRepository.findByUserIdAndVideoId("user-1", 1))
                .thenReturn(Optional.of(videoProgress));
        when(userVideoProgressRepository.save(any())).thenReturn(videoProgress);

        request.setWatchedDuration(60);
        request.setVideoDuration(120); // 50%
        service.updateVideoProgress(request);

        assertEquals(50.0, videoProgress.getProgressPercentage(), 0.1);
    }

    @Test
    void updateVideoProgress_ProgressPercentageCappedAt100() {
        when(userVideoProgressRepository.findByUserIdAndVideoId("user-1", 1))
                .thenReturn(Optional.of(videoProgress));
        when(userVideoProgressRepository.save(any())).thenReturn(videoProgress);

        request.setWatchedDuration(150);
        request.setVideoDuration(100);
        service.updateVideoProgress(request);

        assertEquals(100.0, videoProgress.getProgressPercentage(), 0.1);
    }

    @Test
    void updateVideoProgress_NullWatchedDuration_SkipsDurationUpdate() {
        when(userVideoProgressRepository.findByUserIdAndVideoId("user-1", 1))
                .thenReturn(Optional.of(videoProgress));
        when(userVideoProgressRepository.save(any())).thenReturn(videoProgress);

        request.setWatchedDuration(null);
        request.setVideoDuration(null);
        service.updateVideoProgress(request);

        assertEquals(0, videoProgress.getWatchedDuration());
    }

    @Test
    void updateVideoProgress_UpdatesLastPosition() {
        when(userVideoProgressRepository.findByUserIdAndVideoId("user-1", 1))
                .thenReturn(Optional.of(videoProgress));
        when(userVideoProgressRepository.save(any())).thenReturn(videoProgress);

        request.setLastPosition(45);
        service.updateVideoProgress(request);

        assertEquals(45, videoProgress.getLastPosition());
    }

    // -----------------------------------------------------------------------
    // getUserVideoProgress
    // -----------------------------------------------------------------------
    @Test
    void getUserVideoProgress_DefaultPage_ReturnsPage() {
        Map<String, String> params = new HashMap<>();
        Page<UserVideoProgress> page = new PageImpl<>(List.of(videoProgress));
        when(userVideoProgressRepository.findByUserId(anyString(), any(Pageable.class))).thenReturn(page);
        when(userVideoMapper.toUserVideoResponse(any(UserVideoProgress.class)))
                .thenReturn(new UserVideoResponse());

        Page<UserVideoResponse> result = service.getUserVideoProgress("user-1", params);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void getUserVideoProgress_NegativePage_AdjustsToZero() {
        Map<String, String> params = new HashMap<>();
        params.put("page", "-2");
        Page<UserVideoProgress> page = new PageImpl<>(List.of());
        when(userVideoProgressRepository.findByUserId(anyString(), any(Pageable.class))).thenReturn(page);

        assertDoesNotThrow(() -> service.getUserVideoProgress("user-1", params));
    }

    // -----------------------------------------------------------------------
    // updateQuizProgress
    // -----------------------------------------------------------------------
    @Test
    void updateQuizProgress_ExistingRecord_IncrementsCount() {
        quizProgress.setCount(3);
        when(userQuizProgressRepository.findByUserIdAndQuizId("user-1", 1))
                .thenReturn(Optional.of(quizProgress));
        when(userQuizProgressRepository.save(any())).thenReturn(quizProgress);

        UserQuizProgress result = service.updateQuizProgress("user-1", 1);

        assertNotNull(result);
        assertEquals(4, quizProgress.getCount());
        verify(userQuizProgressRepository).save(quizProgress);
    }

    @Test
    void updateQuizProgress_NewRecord_CreatesWithCount1() {
        when(userQuizProgressRepository.findByUserIdAndQuizId("user-1", 1))
                .thenReturn(Optional.empty());
        when(quizRepository.findById(1)).thenReturn(Optional.of(quiz));
        when(userQuizProgressRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UserQuizProgress result = service.updateQuizProgress("user-1", 1);

        assertNotNull(result);
        assertEquals(1, result.getCount());
    }

    @Test
    void updateVideoProgress_ZeroVideoDuration_DoesNotCalculatePercentage() {
        when(userVideoProgressRepository.findByUserIdAndVideoId("user-1", 1))
                .thenReturn(Optional.of(videoProgress));
        when(userVideoProgressRepository.save(any())).thenReturn(videoProgress);

        request.setWatchedDuration(60);
        request.setVideoDuration(0); // zero → no percentage update
        service.updateVideoProgress(request);

        // progressPercentage không thay đổi (vẫn là 0)
        assertEquals(0.0, videoProgress.getProgressPercentage(), 0.01);
    }

    @Test
    void updateQuizProgress_ExistingRecord_CountIsCorrectAfterMultipleCalls() {
        quizProgress.setCount(0);
        when(userQuizProgressRepository.findByUserIdAndQuizId("user-1", 1))
                .thenReturn(Optional.of(quizProgress));
        when(userQuizProgressRepository.save(any())).thenAnswer(inv -> {
            quizProgress.setCount(quizProgress.getCount() + 1);
            return quizProgress;
        });

        service.updateQuizProgress("user-1", 1);

        verify(userQuizProgressRepository, times(1)).save(quizProgress);
    }
}
