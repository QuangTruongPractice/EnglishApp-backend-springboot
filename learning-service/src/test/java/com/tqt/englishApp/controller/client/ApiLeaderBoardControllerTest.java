package com.tqt.englishApp.controller.client;

import com.tqt.englishApp.entity.UserLearningProfile;
import com.tqt.englishApp.enums.Level;
import com.tqt.englishApp.repository.UserLearningProfileRepository;
import com.tqt.englishApp.service.IdentityClient;
import com.tqt.englishApp.dto.response.UserIdentityResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ApiLeaderBoardController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ApiLeaderBoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserLearningProfileRepository profileRepository;

    @MockitoBean
    private IdentityClient identityClient;

    @Test
    void getSecureWeeklyLeaderboard_Success() throws Exception {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("User1");

        UserLearningProfile profile = UserLearningProfile.builder()
                .userId("User1").weeklyXp(100).level(Level.A1).build();

        when(profileRepository.findTop10ByWeeklyXp()).thenReturn(Collections.singletonList(profile));
        when(profileRepository.findByUserId("User1")).thenReturn(Optional.of(profile));
        when(profileRepository.getWeeklyRank("User1")).thenReturn(1);

        when(identityClient.getUsersByUsernames(anyList())).thenReturn(
                Collections.singletonList(new UserIdentityResponse("id-1", "User1", "John", "Doe")));

        mockMvc.perform(get("/api/secure/leaderboard/weekly").principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.leaderBoard").isArray())
                .andExpect(jsonPath("$.result.currentUser").exists());
    }
}
