package com.tqt.englishApp.service;

import com.tqt.englishApp.dto.request.ApiResponse;
import com.tqt.englishApp.dto.request.AuthenticationRequest;
import com.tqt.englishApp.dto.response.AuthenticationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IdentityAuthServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private IdentityAuthService identityAuthService;

    private AuthenticationRequest authRequest;
    private AuthenticationResponse authResponse;
    private ApiResponse<AuthenticationResponse> apiResponse;

    @BeforeEach
    void setUp() {
        authRequest = new AuthenticationRequest("admin", "password");
        authResponse = new AuthenticationResponse(true, "mock-token");
        apiResponse = new ApiResponse<>();
        apiResponse.setResult(authResponse);
    }

    @Test
    void authenticate_Success() {
        ResponseEntity<ApiResponse<AuthenticationResponse>> responseEntity = ResponseEntity.ok(apiResponse);

        when(restTemplate.exchange(
                eq("http://localhost:8081/identity/api/login"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class))).thenReturn(responseEntity);

        AuthenticationResponse result = identityAuthService.authenticate("admin", "password");

        assertNotNull(result);
        assertTrue(result.isAuthenticated());
        assertEquals("mock-token", result.getToken());
    }

    @Test
    void authenticate_Failure_EmptyBody() {
        ResponseEntity<ApiResponse<AuthenticationResponse>> responseEntity = ResponseEntity.ok(new ApiResponse<>());

        when(restTemplate.exchange(
                eq("http://localhost:8081/identity/api/login"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class))).thenReturn(responseEntity);

        AuthenticationResponse result = identityAuthService.authenticate("admin", "password");

        assertNull(result);
    }

    @Test
    void authenticate_Exception_ReturnsNull() {
        when(restTemplate.exchange(
                eq("http://localhost:8081/identity/api/login"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenThrow(new RuntimeException("Connection failed"));

        AuthenticationResponse result = identityAuthService.authenticate("admin", "password");

        assertNull(result);
    }
}
