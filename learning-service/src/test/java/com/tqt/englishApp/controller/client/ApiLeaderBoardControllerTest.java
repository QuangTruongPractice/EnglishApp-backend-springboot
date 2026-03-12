package com.tqt.englishApp.controller.client;

import com.tqt.englishApp.dto.response.LeaderBoardWrapperResponse;
import com.tqt.englishApp.service.RankingService;
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
    private RankingService rankingService;

    @Test
    void getLeaderBoard_Success() throws Exception {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("user-123");

        LeaderBoardWrapperResponse wrapper = new LeaderBoardWrapperResponse();
        when(rankingService.getLeaderBoardWithCurrentUser("user-123")).thenReturn(wrapper);

        mockMvc.perform(get("/api/secure/leader-board").principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").exists());

        verify(rankingService).getLeaderBoardWithCurrentUser("user-123");
    }
}
