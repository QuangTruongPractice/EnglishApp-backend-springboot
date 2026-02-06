package com.tqt.englishApp.controller;

import com.tqt.englishApp.service.StatsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StatsController.class)
@AutoConfigureMockMvc(addFilters = false)
public class StatsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StatsService statsService;

    @Test
    void statsPage_DefaultCase_Success() throws Exception {
        List<Object[]> stats = new ArrayList<>();
        when(statsService.statsUserByEveryYear()).thenReturn(stats);

        mockMvc.perform(get("/admin/stats"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/stats"))
                .andExpect(model().attribute("userStats", stats))
                .andExpect(model().attribute("selectedType", "default"));
    }

    @Test
    void statsPage_YearCase_WithYear_Success() throws Exception {
        List<Object[]> stats = new ArrayList<>();
        when(statsService.statsUserByYear(2023)).thenReturn(stats);

        mockMvc.perform(get("/admin/stats")
                .param("type", "year")
                .param("year", "2023"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("selectedType", "year"))
                .andExpect(model().attribute("selectedYear", 2023));

        verify(statsService).statsUserByYear(2023);
    }

    @Test
    void statsPage_YearCase_WithoutYear_Success() throws Exception {
        mockMvc.perform(get("/admin/stats")
                .param("type", "year"))
                .andExpect(status().isOk());

        verify(statsService, never()).statsUserByYear(anyInt());
    }

    @Test
    void statsPage_QuarterCase_WithYearAndQuarter_Success() throws Exception {
        List<Object[]> stats = new ArrayList<>();
        when(statsService.statsUserByQuarter(2023, 1)).thenReturn(stats);

        mockMvc.perform(get("/admin/stats")
                .param("type", "quarter")
                .param("year", "2023")
                .param("quarter", "1"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("selectedType", "quarter"))
                .andExpect(model().attribute("selectedQuarter", 1));

        verify(statsService).statsUserByQuarter(2023, 1);
    }

    @Test
    void statsPage_QuarterCase_MissingParams_Success() throws Exception {
        mockMvc.perform(get("/admin/stats")
                .param("type", "quarter")
                .param("year", "2023"))
                .andExpect(status().isOk());

        verify(statsService, never()).statsUserByQuarter(anyInt(), anyInt());
    }

    @Test
    void statsPage_MonthCase_WithYearAndMonth_Success() throws Exception {
        List<Object[]> stats = new ArrayList<>();
        when(statsService.statsUserByMonth(2023, 5)).thenReturn(stats);

        mockMvc.perform(get("/admin/stats")
                .param("type", "month")
                .param("year", "2023")
                .param("month", "5"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("selectedMonth", 5));

        verify(statsService).statsUserByMonth(2023, 5);
    }

    @Test
    void statsPage_MonthCase_MissingParams_Success() throws Exception {
        mockMvc.perform(get("/admin/stats")
                .param("type", "month")
                .param("year", "2023"))
                .andExpect(status().isOk());

        verify(statsService, never()).statsUserByMonth(anyInt(), anyInt());
    }
}
