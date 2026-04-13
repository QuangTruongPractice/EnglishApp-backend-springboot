package com.tqt.englishApp.service;

import com.tqt.englishApp.dto.request.ApiResponse;
import com.tqt.englishApp.dto.request.AuthenticationRequest;
import com.tqt.englishApp.dto.response.AuthenticationResponse;
import com.tqt.englishApp.dto.response.UserIdentityResponse;
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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IdentityClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private IdentityClient identityClient;

    private AuthenticationRequest authRequest;
    private AuthenticationResponse authResponse;
    private ApiResponse<AuthenticationResponse> apiResponse;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(identityClient, "identityServiceBaseUrl", "http://localhost:8080/identity");

        authRequest = new AuthenticationRequest("admin", "password");
        authResponse = new AuthenticationResponse(true, "mock-token");
        apiResponse = new ApiResponse<>();
        apiResponse.setResult(authResponse);
    }

    @Test
    void authenticate_Success() {
        ResponseEntity<ApiResponse<AuthenticationResponse>> responseEntity = ResponseEntity.ok(apiResponse);

        when(restTemplate.exchange(
                eq("http://localhost:8080/identity/api/login"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class))).thenReturn(responseEntity);

        AuthenticationResponse result = identityClient.authenticate("admin", "password");

        assertNotNull(result);
        assertTrue(result.isAuthenticated());
        assertEquals("mock-token", result.getToken());
    }

    @Test
    void authenticate_Failure_EmptyBody() {
        ResponseEntity<ApiResponse<AuthenticationResponse>> responseEntity = ResponseEntity.ok(new ApiResponse<>());

        when(restTemplate.exchange(
                eq("http://localhost:8080/identity/api/login"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class))).thenReturn(responseEntity);

        AuthenticationResponse result = identityClient.authenticate("admin", "password");

        assertNull(result);
    }

    @Test
    void getUsersByUsernames_Success() {
        UserIdentityResponse mockUser = new UserIdentityResponse("id-1", "user1", "John", "Doe");
        ApiResponse<List<UserIdentityResponse>> apiListResponse = new ApiResponse<>();
        apiListResponse.setResult(Arrays.asList(mockUser));

        ResponseEntity<ApiResponse<List<UserIdentityResponse>>> responseEntity = ResponseEntity.ok(apiListResponse);

        when(restTemplate.exchange(
                eq("http://localhost:8080/identity/api/list"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class))).thenReturn(responseEntity);

        List<UserIdentityResponse> result = identityClient.getUsersByUsernames(Arrays.asList("user1"));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getFullName());
    }

    @Test
    void getUsersByUsernames_EmptyOrNullInput() {
        assertTrue(identityClient.getUsersByUsernames(null).isEmpty());
        assertTrue(identityClient.getUsersByUsernames(Arrays.asList()).isEmpty());
    }
}
