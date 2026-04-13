package com.tqt.englishApp.controller.client;

import com.tqt.englishApp.dto.response.SessionResponse;
import com.tqt.englishApp.dto.response.SubmitQuizResponse;
import com.tqt.englishApp.service.SessionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ApiSessionController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ApiSessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SessionService sessionService;

    @Test
    void createSession_Success() throws Exception {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("user-123");

        SessionResponse sessionResponse = new SessionResponse();
        sessionResponse.setId(1);
        when(sessionService.getOrCreateSession("user-123")).thenReturn(sessionResponse);

        mockMvc.perform(post("/api/secure/sessions/daily").principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").value(1));
    }

    @Test
    void submitQuiz_Success() throws Exception {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("user-123");

        SubmitQuizResponse submitResponse = SubmitQuizResponse.builder().xpAwarded(5).build();
        when(sessionService.submitQuiz(eq(1), eq(10), eq("user-123"), eq(true), any())).thenReturn(submitResponse);

        mockMvc.perform(post("/api/secure/sessions/1/quiz/10/submit")
                        .param("isCorrect", "true")
                        .principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.xpAwarded").value(5));
    }

    @Test
    void checkLevelUp_Success() throws Exception {
        when(sessionService.checkLevelUp(1)).thenReturn(false);

        mockMvc.perform(get("/api/sessions/1/levelup-check"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(false));
    }
}
