package com.tqt.englishApp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tqt.englishApp.dto.request.VideoProgressRequest;
import com.tqt.englishApp.dto.request.VocabularyProgressRequest;
import com.tqt.englishApp.dto.response.UserResponse;
import com.tqt.englishApp.dto.response.UserVideoResponse;
import com.tqt.englishApp.dto.response.UserVocabularyResponse;
import com.tqt.englishApp.entity.UserQuizProgress;
import com.tqt.englishApp.entity.UserVideoProgress;
import com.tqt.englishApp.entity.UserVocabularyProgress;
import com.tqt.englishApp.service.LearningProgressService;
import com.tqt.englishApp.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ApiLearningProgressController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ApiLearningProgressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LearningProgressService learningProgressService;

    @MockitoBean
    private UserService userService;

    @Test
    void updateVideoProgress_Success() throws Exception {
        VideoProgressRequest request = new VideoProgressRequest();
        UserVideoProgress progress = new UserVideoProgress();

        when(learningProgressService.updateVideoProgress(any())).thenReturn(progress);

        mockMvc.perform(post("/api/learning-progress/video")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").exists());
    }

    @Test
    void getUserVocabularyProgress_Success() throws Exception {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("user1");
        when(userService.findUserByUsername("user1")).thenReturn(UserResponse.builder().id("123").build());

        List<UserVocabularyResponse> list = Collections.emptyList();
        when(learningProgressService.getUserVocabularyProgress("123")).thenReturn(list);

        mockMvc.perform(get("/api/secure/learning-progress/vocabulary").principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").isArray());
    }

    @Test
    void getUserVideoProgress_Success() throws Exception {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("user2");
        when(userService.findUserByUsername("user2")).thenReturn(UserResponse.builder().id("456").build());

        List<UserVideoResponse> list = Collections.emptyList();
        when(learningProgressService.getUserVideoProgress("456")).thenReturn(list);

        mockMvc.perform(get("/api/secure/learning-progress/video").principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").isArray());
    }

    @Test
    void markFlashcardViewed_Success() throws Exception {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("user1");
        when(userService.findUserByUsername("user1")).thenReturn(UserResponse.builder().id("123").build());

        UserVocabularyProgress progress = new UserVocabularyProgress();
        when(learningProgressService.updateVocabularyProgress(any(VocabularyProgressRequest.class)))
                .thenReturn(progress);

        mockMvc.perform(put("/api/secure/vocabulary/1/view-flashcard").principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").exists());

        verify(learningProgressService).updateVocabularyProgress(argThat(
                req -> "123".equals(req.getUserId()) && req.getVocabularyId() == 1 && req.getViewedFlashcard()));
    }

    @Test
    void markPronunciationPracticed_Success() throws Exception {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("user1");
        when(userService.findUserByUsername("user1")).thenReturn(UserResponse.builder().id("123").build());

        UserVocabularyProgress progress = new UserVocabularyProgress();
        when(learningProgressService.updateVocabularyProgress(any(VocabularyProgressRequest.class)))
                .thenReturn(progress);

        mockMvc.perform(put("/api/secure/vocabulary/1/practice-pronunciation").principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").exists());

        verify(learningProgressService).updateVocabularyProgress(argThat(
                req -> "123".equals(req.getUserId()) && req.getVocabularyId() == 1 && req.getPracticedPronunciation()));
    }

    @Test
    void updateVideoProgressByPath_Success() throws Exception {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("user1");
        when(userService.findUserByUsername("user1")).thenReturn(UserResponse.builder().id("123").build());

        VideoProgressRequest request = VideoProgressRequest.builder()
                .watchedDuration(100)
                .videoDuration(200)
                .lastPosition(50)
                .build();

        UserVideoProgress progress = new UserVideoProgress();
        when(learningProgressService.updateVideoProgress(any(VideoProgressRequest.class))).thenReturn(progress);

        mockMvc.perform(put("/api/secure/video/1/update-progress")
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").exists());

        verify(learningProgressService).updateVideoProgress(argThat(
                req -> "123".equals(req.getUserId()) && req.getVideoId() == 1 && req.getWatchedDuration() == 100));
    }

    @Test
    void updateQuizProgress_Success() throws Exception {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("user1");
        when(userService.findUserByUsername("user1")).thenReturn(UserResponse.builder().id("123").build());

        UserQuizProgress progress = new UserQuizProgress();
        when(learningProgressService.updateProgress("123", 1)).thenReturn(progress);

        mockMvc.perform(put("/api/secure/quiz/1/quiz-progress").principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").exists());
    }
}
