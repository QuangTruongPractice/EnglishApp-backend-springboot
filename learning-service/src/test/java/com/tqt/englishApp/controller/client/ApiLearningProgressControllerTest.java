package com.tqt.englishApp.controller.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tqt.englishApp.dto.request.VideoProgressRequest;
import com.tqt.englishApp.dto.request.VocabularyProgressRequest;
import com.tqt.englishApp.dto.response.UserVideoResponse;
import com.tqt.englishApp.dto.response.vocabulary.UserVocabularyResponse;
import com.tqt.englishApp.entity.UserQuizProgress;
import com.tqt.englishApp.entity.UserVideoProgress;
import com.tqt.englishApp.entity.UserVocabularyProgress;
import com.tqt.englishApp.service.ContentProgressService;
import com.tqt.englishApp.service.VocabularyLearningService;
import com.tqt.englishApp.service.VocabularySelectionService;
import com.tqt.englishApp.mapper.VocabularyMapper;
import com.tqt.englishApp.mapper.WordMeaningMapper;
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
        private VocabularyLearningService vocabularyLearningService;

        @MockitoBean
        private ContentProgressService contentProgressService;

        @MockitoBean
        private VocabularySelectionService vocabularySelectionService;

        @MockitoBean
        private WordMeaningMapper wordMeaningMapper;

        @MockitoBean
        private VocabularyMapper vocabularyMapper;

        @Test
        void updateVideoProgress_Success() throws Exception {
                VideoProgressRequest request = new VideoProgressRequest();
                UserVideoProgress progress = new UserVideoProgress();

                when(contentProgressService.updateVideoProgress(any())).thenReturn(progress);

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

                List<UserVocabularyResponse> list = Collections.emptyList();
                when(vocabularyLearningService.getUserVocabularyProgress("user1")).thenReturn(list);

                mockMvc.perform(get("/api/secure/learning-progress/vocabulary").principal(principal))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.result").isArray());
        }

        @Test
        void getUserVideoProgress_Success() throws Exception {
                Principal principal = mock(Principal.class);
                when(principal.getName()).thenReturn("user2");

                List<UserVideoResponse> list = Collections.emptyList();
                when(contentProgressService.getUserVideoProgress("user2")).thenReturn(list);

                mockMvc.perform(get("/api/secure/learning-progress/video").principal(principal))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.result").isArray());
        }

        @Test
        void updateQuizResult_Success() throws Exception {
                Principal principal = mock(Principal.class);
                when(principal.getName()).thenReturn("user1");

                UserVocabularyProgress progress = new UserVocabularyProgress();
                when(vocabularyLearningService.updateVocabularyProgress(any(VocabularyProgressRequest.class)))
                                .thenReturn(progress);

                mockMvc.perform(put("/api/secure/vocabulary/meaning/1/quiz-result")
                                .principal(principal)
                                .param("isCorrect", "true"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.result").exists());

                verify(vocabularyLearningService).updateVocabularyProgress(argThat(
                                req -> "user1".equals(req.getUserId()) && req.getMeaningId() == 1
                                                && req.getIsCorrect()));
        }

        // markPronunciationPracticed_Success was removed because the API endpoint
        // changed/was consolidated

        @Test
        void updateVideoProgressByPath_Success() throws Exception {
                Principal principal = mock(Principal.class);
                when(principal.getName()).thenReturn("user1");

                VideoProgressRequest request = VideoProgressRequest.builder()
                                .watchedDuration(100)
                                .videoDuration(200)
                                .lastPosition(50)
                                .build();

                UserVideoProgress progress = new UserVideoProgress();
                when(contentProgressService.updateVideoProgress(any(VideoProgressRequest.class))).thenReturn(progress);

                mockMvc.perform(put("/api/secure/video/1/update-progress")
                                .principal(principal)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.result").exists());

                verify(contentProgressService).updateVideoProgress(argThat(
                                req -> "user1".equals(req.getUserId()) && req.getVideoId() == 1
                                                && req.getWatchedDuration() == 100));
        }

        @Test
        void updateQuizProgress_Success() throws Exception {
                Principal principal = mock(Principal.class);
                when(principal.getName()).thenReturn("user1");

                UserQuizProgress progress = new UserQuizProgress();
                when(contentProgressService.updateQuizProgress("user1", 1)).thenReturn(progress);

                mockMvc.perform(put("/api/secure/quiz/1/quiz-progress").principal(principal))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.result").exists());
        }
}
