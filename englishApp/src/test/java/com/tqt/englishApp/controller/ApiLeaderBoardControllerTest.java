package com.tqt.englishApp.controller;

import com.tqt.englishApp.dto.response.LeaderBoardWrapperResponse;
import com.tqt.englishApp.dto.response.UserResponse;
import com.tqt.englishApp.service.LearningProgressService;
import com.tqt.englishApp.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ApiLeaderBoardController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ApiLeaderBoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LearningProgressService learningProgressService;

    @MockitoBean
    private UserService userService;

    @Test
    void getLeaderBoard_Success() throws Exception {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("user1");
        when(userService.findUserByUsername("user1")).thenReturn(UserResponse.builder().id("123").build());

        LeaderBoardWrapperResponse wrapper = new LeaderBoardWrapperResponse();
        when(learningProgressService.getLeaderBoardWithCurrentUser("123")).thenReturn(wrapper);

        mockMvc.perform(get("/api/secure/leader-board").principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").exists());
    }
}
