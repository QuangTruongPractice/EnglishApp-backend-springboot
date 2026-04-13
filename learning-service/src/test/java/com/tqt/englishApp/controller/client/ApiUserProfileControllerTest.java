package com.tqt.englishApp.controller.client;

import com.tqt.englishApp.dto.response.UserLearningProfileResponse;
import com.tqt.englishApp.entity.UserLearningProfile;
import com.tqt.englishApp.mapper.UserLearningProfileMapper;
import com.tqt.englishApp.service.UserLearningProfileService;
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

@WebMvcTest(ApiUserProfileController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ApiUserProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserLearningProfileService userLearningProfileService;

    @MockitoBean
    private UserLearningProfileMapper userLearningProfileMapper;

    @Test
    void getProfile_Success() throws Exception {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("user-123");

        UserLearningProfile profile = new UserLearningProfile();
        UserLearningProfileResponse profileResponse = new UserLearningProfileResponse();
        profileResponse.setUserId("user-123");

        when(userLearningProfileService.getProfile("user-123")).thenReturn(profile);
        when(userLearningProfileMapper.toResponse(profile)).thenReturn(profileResponse);

        mockMvc.perform(get("/api/secure/learning-profile").principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.userId").value("user-123"));
    }
}
