package com.tqt.englishApp.controller;

import com.tqt.englishApp.controller.client.ApiAuthController;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tqt.englishApp.dto.request.AuthenticationRequest;
import com.tqt.englishApp.dto.request.GoogleAuthRequest;
import com.tqt.englishApp.dto.request.UserUpdateRequest;
import com.tqt.englishApp.dto.response.RoleResponse;
import com.tqt.englishApp.dto.response.UserResponse;
import com.tqt.englishApp.service.AuthenticateService;
import com.tqt.englishApp.service.UserService;
import com.tqt.englishApp.utils.JwtUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ApiAuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ApiAuthControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockitoBean
        private AuthenticateService authenticateService;

        @MockitoBean
        private UserService userService;

        private MockedStatic<JwtUtils> mockedJwtUtils;

        @BeforeEach
        void setUp() {
                mockedJwtUtils = mockStatic(JwtUtils.class);
        }

        @AfterEach
        void tearDown() {
                mockedJwtUtils.close();
        }

        @Test
        void login_Success() throws Exception {
                AuthenticationRequest request = new AuthenticationRequest("user", "pass");
                UserResponse userResponse = UserResponse.builder()
                                .username("user")
                                .isActive(true)
                                .roles(Set.of(RoleResponse.builder().name("USER").build()))
                                .build();

                when(authenticateService.authentication(any())).thenReturn(true);
                when(userService.findUserByUsername("user")).thenReturn(userResponse);
                mockedJwtUtils.when(() -> JwtUtils.generateToken(anyString(), anyString())).thenReturn("mocked-token");

                mockMvc.perform(post("/api/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.result.authenticated").value(true))
                                .andExpect(jsonPath("$.result.token").value("mocked-token"))
                                .andExpect(jsonPath("$.message").value("Đăng nhập thành công"));
        }

        @Test
        void login_InactiveAccount_Failure() throws Exception {
                AuthenticationRequest request = new AuthenticationRequest("user", "pass");
                UserResponse userResponse = UserResponse.builder()
                                .username("user")
                                .isActive(false)
                                .roles(Set.of(RoleResponse.builder().name("USER").build()))
                                .build();

                when(authenticateService.authentication(any())).thenReturn(true);
                when(userService.findUserByUsername("user")).thenReturn(userResponse);

                mockMvc.perform(post("/api/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.result.authenticated").value(false))
                                .andExpect(jsonPath("$.code").value(403))
                                .andExpect(jsonPath("$.message").value("Tài khoản chưa được duyệt hoặc đã bị khóa"));
        }

        @Test
        void login_InvalidCredentials_Failure() throws Exception {
                AuthenticationRequest request = new AuthenticationRequest("user", "wrong");
                when(authenticateService.authentication(any())).thenReturn(false);

                mockMvc.perform(post("/api/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.result.authenticated").value(false))
                                .andExpect(jsonPath("$.code").value(401))
                                .andExpect(jsonPath("$.message").value("Sai tên đăng nhập hoặc mật khẩu"));
        }

        @Test
        void login_JwtGenerationError_Failure() throws Exception {
                AuthenticationRequest request = new AuthenticationRequest("user", "pass");
                UserResponse userResponse = UserResponse.builder()
                                .username("user")
                                .isActive(true)
                                .roles(Set.of(RoleResponse.builder().name("USER").build()))
                                .build();

                when(authenticateService.authentication(any())).thenReturn(true);
                when(userService.findUserByUsername("user")).thenReturn(userResponse);
                mockedJwtUtils.when(() -> JwtUtils.generateToken(anyString(), anyString()))
                                .thenThrow(new RuntimeException("JWT Error"));

                mockMvc.perform(post("/api/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(500))
                                .andExpect(jsonPath("$.message").value("Lỗi khi tạo JWT: JWT Error"));
        }

        @Test
        void getProfile_Success() throws Exception {
                Principal principal = mock(Principal.class);
                when(principal.getName()).thenReturn("user1");
                UserResponse userResponse = UserResponse.builder().username("user1").build();
                when(userService.findUserByUsername("user1")).thenReturn(userResponse);

                mockMvc.perform(get("/api/secure/profile").principal(principal))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.username").value("user1"));
        }

        @Test
        void updateProfile_Success() throws Exception {
                Principal principal = mock(Principal.class);
                when(principal.getName()).thenReturn("user1");
                UserResponse userResponse = UserResponse.builder().id("123").username("user1").build();
                when(userService.findUserByUsername("user1")).thenReturn(userResponse);

                UserUpdateRequest updateRequest = new UserUpdateRequest();
                updateRequest.setFirstName("New");
                updateRequest.setLastName("Name");

                when(userService.updateUser(eq("123"), any(UserUpdateRequest.class))).thenReturn(userResponse);

                mockMvc.perform(put("/api/secure/profile")
                                .principal(principal)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .param("firstName", "New")
                                .param("lastName", "Name"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.username").value("user1"));
        }

        @Test
        void googleSignIn_Success() throws Exception {
                GoogleAuthRequest request = new GoogleAuthRequest();
                request.setEmail("user@gmail.com");

                UserResponse userResponse = UserResponse.builder()
                                .email("user@gmail.com")
                                .isActive(true)
                                .roles(Set.of(RoleResponse.builder().name("USER").build()))
                                .build();

                when(authenticateService.googleAuth(any())).thenReturn(true);
                when(userService.findUserByEmail("user@gmail.com")).thenReturn(userResponse);
                mockedJwtUtils.when(() -> JwtUtils.generateToken(anyString(), anyString())).thenReturn("google-token");

                mockMvc.perform(post("/api/auth/google-signin")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.result.authenticated").value(true))
                                .andExpect(jsonPath("$.result.token").value("google-token"));
        }

        @Test
        void googleSignIn_InactiveAccount_Failure() throws Exception {
                GoogleAuthRequest request = new GoogleAuthRequest();
                request.setEmail("user@gmail.com");

                UserResponse userResponse = UserResponse.builder()
                                .email("user@gmail.com")
                                .isActive(false)
                                .roles(Set.of(RoleResponse.builder().name("USER").build()))
                                .build();

                when(authenticateService.googleAuth(any())).thenReturn(true);
                when(userService.findUserByEmail("user@gmail.com")).thenReturn(userResponse);

                mockMvc.perform(post("/api/auth/google-signin")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.result.authenticated").value(false))
                                .andExpect(jsonPath("$.code").value(403));
        }

        @Test
        void googleSignIn_FailedAuth_Failure() throws Exception {
                GoogleAuthRequest request = new GoogleAuthRequest();
                request.setEmail("user@gmail.com");

                when(authenticateService.googleAuth(any())).thenReturn(false);

                mockMvc.perform(post("/api/auth/google-signin")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.message").value("Xác thực Google thất bại"));
        }

        @Test
        void googleSignIn_JwtError_Failure() throws Exception {
                GoogleAuthRequest request = new GoogleAuthRequest();
                request.setEmail("user@gmail.com");

                UserResponse userResponse = UserResponse.builder()
                                .email("user@gmail.com")
                                .isActive(true)
                                .roles(Set.of(RoleResponse.builder().name("USER").build()))
                                .build();

                when(authenticateService.googleAuth(any())).thenReturn(true);
                when(userService.findUserByEmail("user@gmail.com")).thenReturn(userResponse);
                mockedJwtUtils.when(() -> JwtUtils.generateToken(anyString(), anyString()))
                                .thenThrow(new RuntimeException("JWT Error"));

                mockMvc.perform(post("/api/auth/google-signin")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(500));
        }
}
