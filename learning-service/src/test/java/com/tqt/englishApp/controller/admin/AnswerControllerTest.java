package com.tqt.englishApp.controller.admin;

import com.tqt.englishApp.dto.request.AnswerRequest;
import com.tqt.englishApp.dto.response.AnswerResponse;
import com.tqt.englishApp.dto.response.quiz.BaseQuizResponse;
import com.tqt.englishApp.service.AnswerService;
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

@WebMvcTest(AnswerController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AnswerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AnswerService answerService;

    @MockitoBean
    private QuizService quizService;

    @Test
    void listAnswers_WithoutParams_Success() throws Exception {
        Page<AnswerResponse> page = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        when(answerService.getAnswer(anyMap())).thenReturn(page);

        mockMvc.perform(get("/admin/answers"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/answers"))
                .andExpect(model().attributeExists("answers"))
                .andExpect(model().attribute("currentPage", 1));
    }

    @Test
    void listAnswers_WithParams_Success() throws Exception {
        Page<AnswerResponse> page = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        when(answerService.getAnswer(anyMap())).thenReturn(page);

        mockMvc.perform(get("/admin/answers")
                .param("answer", "Correct")
                .param("page", "2"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/answers"))
                .andExpect(model().attribute("currentPage", 2));

        verify(answerService)
                .getAnswer(argThat(params -> "Correct".equals(params.get("answer")) && "2".equals(params.get("page"))));
    }

    @Test
    void answersForm_Success() throws Exception {
        Page<BaseQuizResponse> quizPage = new PageImpl<>(Collections.emptyList());
        when(quizService.getQuiz(anyMap())).thenReturn(quizPage);

        mockMvc.perform(get("/admin/answers/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/answers_form"))
                .andExpect(model().attributeExists("answers"))
                .andExpect(model().attribute("quizs", quizPage.getContent()));
    }

    @Test
    void addAnswers_ValidationErrors() throws Exception {
        Page<BaseQuizResponse> quizPage = new PageImpl<>(Collections.emptyList());
        when(quizService.getQuiz(anyMap())).thenReturn(quizPage);

        mockMvc.perform(post("/admin/answers/add")
                .param("answer", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/answers_form"));

        verifyNoInteractions(answerService);
    }

    @Test
    void addAnswers_Create_Success() throws Exception {
        AnswerRequest request = new AnswerRequest();
        request.setAnswer("New Answer");

        mockMvc.perform(post("/admin/answers/add")
                .flashAttr("answers", request))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/answers"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(answerService).createAnswer(any(AnswerRequest.class));
    }

    @Test
    void addAnswers_Update_Success() throws Exception {
        AnswerRequest request = new AnswerRequest();
        request.setId(1);
        request.setAnswer("Updated Answer");

        mockMvc.perform(post("/admin/answers/add")
                .flashAttr("answers", request))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/answers"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(answerService).updateAnswer(any(AnswerRequest.class), eq(1));
    }

    @Test
    void updateAnswers_Success() throws Exception {
        when(answerService.getAnswerRequestById(anyInt())).thenReturn(new AnswerRequest());
        Page<BaseQuizResponse> quizPage = new PageImpl<>(Collections.emptyList());
        when(quizService.getQuiz(anyMap())).thenReturn(quizPage);

        mockMvc.perform(get("/admin/answers/edit/{answerId}", 1))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/answers_form"))
                .andExpect(model().attributeExists("answers"))
                .andExpect(model().attribute("quizs", quizPage.getContent()));
    }

    @Test
    void deleteAnswers_Success() throws Exception {
        mockMvc.perform(post("/admin/answers/delete/{id}", 1))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/answers"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(answerService).deleteAnswer(1);
    }
}
