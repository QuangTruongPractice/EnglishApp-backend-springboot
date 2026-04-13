package com.tqt.englishApp.controller.client;

import com.tqt.englishApp.dto.response.PlacementQuizResponse;
import com.tqt.englishApp.service.PlacementTestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ApiPlacementController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ApiPlacementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PlacementTestService placementTestService;

    @Test
    void generateQuiz_Success() throws Exception {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("user-123");

        PlacementQuizResponse quizResponse = new PlacementQuizResponse();
        quizResponse.setQuestions(Collections.emptyList());
        when(placementTestService.generatePlacementQuiz("user-123")).thenReturn(quizResponse);

        mockMvc.perform(get("/api/secure/placement/generate").principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").exists());
    }
}
