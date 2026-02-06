package com.tqt.englishApp.controller;

import com.tqt.englishApp.dto.request.QuizRequest;
import com.tqt.englishApp.dto.response.QuizResponse;
import com.tqt.englishApp.service.QuizService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(QuizController.class)
@AutoConfigureMockMvc(addFilters = false)
public class QuizControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private QuizService quizService;

    @Test
    void listQuizs_WithoutParams_Success() throws Exception {
        Page<QuizResponse> page = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 8), 0);
        when(quizService.getQuiz(anyMap())).thenReturn(page);

        mockMvc.perform(get("/admin/quizs"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/quizs"))
                .andExpect(model().attributeExists("quizs"))
                .andExpect(model().attribute("currentPage", 1));
    }

    @Test
    void listQuizs_WithParams_Success() throws Exception {
        Page<QuizResponse> page = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 8), 0);
        when(quizService.getQuiz(anyMap())).thenReturn(page);

        mockMvc.perform(get("/admin/quizs")
                .param("question", "What")
                .param("page", "2"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/quizs"))
                .andExpect(model().attribute("currentPage", 2));

        verify(quizService)
                .getQuiz(argThat(params -> "What".equals(params.get("question")) && "2".equals(params.get("page"))));
    }

    @Test
    void quizsForm_Success() throws Exception {
        mockMvc.perform(get("/admin/quizs/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/quizs_form"))
                .andExpect(model().attributeExists("quizs"));
    }

    @Test
    void addQuizs_ValidationErrors() throws Exception {
        QuizRequest request = new QuizRequest();
        request.setQuestion("");

        mockMvc.perform(post("/admin/quizs/add")
                .flashAttr("quizs", request))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/quizs"));

        verify(quizService).createQuiz(any(QuizRequest.class));
    }

    @Test
    void addQuizs_Create_Success() throws Exception {
        QuizRequest request = new QuizRequest();
        request.setQuestion("New Quiz");

        mockMvc.perform(post("/admin/quizs/add")
                .flashAttr("quizs", request))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/quizs"));

        verify(quizService).createQuiz(any(QuizRequest.class));
    }

    @Test
    void addQuizs_Update_Success() throws Exception {
        QuizRequest request = new QuizRequest();
        request.setId(1);
        request.setQuestion("Updated Quiz");

        mockMvc.perform(post("/admin/quizs/add")
                .flashAttr("quizs", request))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/quizs"));

        verify(quizService).updateQuiz(any(QuizRequest.class), eq(1));
    }

    @Test
    void updateQuizs_Success() throws Exception {
        when(quizService.getQuizById(anyInt())).thenReturn(new QuizResponse());

        mockMvc.perform(get("/admin/quizs/edit/{quizId}", 1))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/quizs_form"))
                .andExpect(model().attributeExists("quizs"));
    }

    @Test
    void deleteQuizs_Success() throws Exception {
        mockMvc.perform(post("/admin/quizs/delete/{id}", 1))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/quizs"));

        verify(quizService).deleteQuiz(1);
    }
}
