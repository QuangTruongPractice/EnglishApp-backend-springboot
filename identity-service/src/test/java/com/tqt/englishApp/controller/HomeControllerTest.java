package com.tqt.englishApp.controller;

import com.tqt.englishApp.repository.UserRepository;
import com.tqt.englishApp.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HomeController.class)
@AutoConfigureMockMvc(addFilters = false)
public class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private StatsService statsService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void home_Success() throws Exception {
        List<Object[]> userStats = new ArrayList<>();
        when(statsService.statsUserByYear(anyInt())).thenReturn(userStats);
        when(userRepository.countActiveUsers()).thenReturn(10L);

        mockMvc.perform(get("/admin/"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/home"))
                .andExpect(model().attribute("userNum", 10L))
                .andExpect(model().attribute("userStats", userStats));
    }

    @Test
    void login_Success() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }
}
