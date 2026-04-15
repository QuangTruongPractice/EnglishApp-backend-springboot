package com.tqt.englishApp.controller;

import com.tqt.englishApp.controller.client.ApiUserController;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tqt.englishApp.dto.request.ChangePasswordRequest;
import com.tqt.englishApp.dto.request.OtpVerifiedRequest;
import com.tqt.englishApp.dto.request.ResetPasswordRequest;
import com.tqt.englishApp.dto.request.UserCreationRequest;
import com.tqt.englishApp.dto.request.UserUpdateRequest;
import com.tqt.englishApp.dto.response.UserResponse;
import com.tqt.englishApp.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ApiUserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ApiUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    void resetPassword_Success() throws Exception {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setEmail("user@test.com");

        mockMvc.perform(post("/api/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Gửi yêu cầu thành công"));

        verify(userService).resetPassword(any(ResetPasswordRequest.class));
    }

    @Test
    void verifiedOtp_Success() throws Exception {
        OtpVerifiedRequest request = new OtpVerifiedRequest();
        request.setEmail("user@test.com");
        request.setOtp("123456");

        when(userService.optVerifiedRequest(any())).thenReturn("OTP Verified");

        mockMvc.perform(post("/api/verified-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OTP Verified"));
    }

    @Test
    void changePassword_Success() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setEmail("user@test.com");
        request.setPassword("newpass");

        UserResponse response = UserResponse.builder().username("user").build();
        when(userService.changePassword(any())).thenReturn(response);

        mockMvc.perform(post("/api/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.username").value("user"));
    }

    @Test
    void register_Success() throws Exception {
        UserResponse response = UserResponse.builder().username("newuser").build();
        when(userService.createUser(any(UserCreationRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/register")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("username", "newuser")
                .param("password", "pass123")
                .param("firstName", "John")
                .param("lastName", "Doe")
                .param("email", "john@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.username").value("newuser"));
    }

    @Test
    void getUsers_Admin_Success() throws Exception {
        Page<UserResponse> page = new PageImpl<>(Collections.emptyList());
        when(userService.getUsers(anyMap())).thenReturn(page);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content").isArray());
    }

    @Test
    void getUser_Admin_Success() throws Exception {
        UserResponse response = UserResponse.builder().id("1").username("user1").build();
        when(userService.getUserById("1")).thenReturn(response);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.username").value("user1"));
    }

    @Test
    void updateUser_Admin_Success() throws Exception {
        UserResponse response = UserResponse.builder().id("1").username("user1").build();
        when(userService.updateUser(eq("1"), any(UserUpdateRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("firstName", "Updated"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.username").value("user1"));
    }

    @Test
    void deleteUser_Admin_Success() throws Exception {
        UserResponse response = UserResponse.builder().id("1").username("user1").isActive(false).build();
        when(userService.deactivateUser("1")).thenReturn(response);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.isActive").value(false));
    }
}
