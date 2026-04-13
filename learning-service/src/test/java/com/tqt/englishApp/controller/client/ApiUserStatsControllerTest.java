package com.tqt.englishApp.controller.client;

import com.tqt.englishApp.dto.response.LearningSummaryResponse;
import com.tqt.englishApp.dto.response.StreakCalendarResponse;
import com.tqt.englishApp.service.UserStatsService;
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

@WebMvcTest(ApiUserStatsController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ApiUserStatsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserStatsService userStatsService;

    @Test
    void getStreakCalendar_Success() throws Exception {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("user-123");

        StreakCalendarResponse calendarResponse = new StreakCalendarResponse();
        when(userStatsService.getStreakCalendar("user-123", 4, 2026)).thenReturn(calendarResponse);

        mockMvc.perform(get("/api/secure/stats/streak-calendar")
                        .param("month", "4")
                        .param("year", "2026")
                        .principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").exists());
    }

    @Test
    void getLearningSummary_Success() throws Exception {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("user-123");

        LearningSummaryResponse summaryResponse = new LearningSummaryResponse();
        when(userStatsService.getLearningSummary("user-123")).thenReturn(summaryResponse);

        mockMvc.perform(get("/api/secure/stats/summary").principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").exists());
    }
}
