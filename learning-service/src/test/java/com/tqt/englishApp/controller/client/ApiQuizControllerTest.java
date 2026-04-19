package com.tqt.englishApp.controller.client;

import com.tqt.englishApp.dto.response.quiz.BaseQuizResponse;
import com.tqt.englishApp.dto.response.quiz.QuizDetailResponse;
import com.tqt.englishApp.service.QuizService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.tqt.englishApp.service.QuizGenerateService;
import com.tqt.englishApp.service.VocabularyLearningService;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ApiQuizController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ApiQuizControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private QuizService quizService;

    @MockitoBean
    private QuizGenerateService quizGenerateService;

    @MockitoBean
    private VocabularyLearningService vocabularyLearningService;

    @Test
    void getQuizzes_Success() throws Exception {
        Page<BaseQuizResponse> page = new PageImpl<>(Collections.emptyList());
        when(quizService.getQuiz(anyMap())).thenReturn(page);

        mockMvc.perform(get("/api/quiz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content").isArray());
    }

    @Test
    void getQuiz_Success() throws Exception {
        QuizDetailResponse response = new QuizDetailResponse();
        when(quizService.getQuizById(1)).thenReturn(response);

        mockMvc.perform(get("/api/quiz/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").exists());
    }
}
