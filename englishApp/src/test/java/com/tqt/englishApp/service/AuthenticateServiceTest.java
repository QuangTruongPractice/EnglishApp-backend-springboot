package com.tqt.englishApp.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.tqt.englishApp.dto.request.AuthenticationRequest;
import com.tqt.englishApp.dto.request.GoogleAuthRequest;
import com.tqt.englishApp.entity.User;
import com.tqt.englishApp.exception.AppException;
import com.tqt.englishApp.exception.ErrorCode;
import com.tqt.englishApp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticateServiceTest {

    @Spy
    @InjectMocks
    private AuthenticateService authenticateService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    private User user;
    private AuthenticationRequest authRequest;

    @BeforeEach
    void init() {
        user = User.builder()
                .username("testuser")
                .password("encodedPassword")
                .build();

        authRequest = new AuthenticationRequest("testuser", "rawPassword");
    }

    @Test
    void authentication_Success() {
        when(userRepository.findUserByUsername("testuser")).thenReturn(user);
        when(passwordEncoder.matches("rawPassword", "encodedPassword")).thenReturn(true);

        boolean result = authenticateService.authentication(authRequest);

        assertTrue(result);
        verify(userRepository).findUserByUsername("testuser");
        verify(passwordEncoder).matches("rawPassword", "encodedPassword");
    }

    @Test
    void authentication_UserNotFound() {
        when(userRepository.findUserByUsername("testuser")).thenReturn(null);

        AppException exception = assertThrows(AppException.class,
                () -> authenticateService.authentication(authRequest));

        assertEquals(ErrorCode.USER_NOT_EXISTED, exception.getErrorCode());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void authentication_WrongPassword() {
        when(userRepository.findUserByUsername("testuser")).thenReturn(user);
        when(passwordEncoder.matches("rawPassword", "encodedPassword")).thenReturn(false);

        AppException exception = assertThrows(AppException.class,
                () -> authenticateService.authentication(authRequest));

        assertEquals(ErrorCode.UNAUTHENTICATED, exception.getErrorCode());
    }

    @Test
    void googleAuth_Success() throws GeneralSecurityException, IOException {
        GoogleAuthRequest request = new GoogleAuthRequest("validToken", "test@gmail.com");

        GoogleIdTokenVerifier verifier = mock(GoogleIdTokenVerifier.class);
        GoogleIdToken idToken = mock(GoogleIdToken.class);
        GoogleIdToken.Payload payload = mock(GoogleIdToken.Payload.class);

        doReturn(verifier).when(authenticateService).getVerifier();
        when(verifier.verify(anyString())).thenReturn(idToken);
        when(idToken.getPayload()).thenReturn(payload);
        when(payload.getEmail()).thenReturn("test@gmail.com");
        when(payload.get("given_name")).thenReturn("First");
        when(payload.get("family_name")).thenReturn("Last");
        when(payload.get("picture")).thenReturn("http://pic.url");
        when(payload.get("name")).thenReturn("Full Name");

        boolean result = authenticateService.googleAuth(request);

        assertTrue(result);
        verify(userService).createGoogleUser(eq("test@gmail.com"), eq("First"), eq("Last"), eq("http://pic.url"));
    }

    @Test
    void googleAuth_Success_UseFullName() throws GeneralSecurityException, IOException {
        GoogleAuthRequest request = new GoogleAuthRequest("validToken", "test@gmail.com");

        GoogleIdTokenVerifier verifier = mock(GoogleIdTokenVerifier.class);
        GoogleIdToken idToken = mock(GoogleIdToken.class);
        GoogleIdToken.Payload payload = mock(GoogleIdToken.Payload.class);

        doReturn(verifier).when(authenticateService).getVerifier();
        when(verifier.verify(anyString())).thenReturn(idToken);
        when(idToken.getPayload()).thenReturn(payload);
        when(payload.getEmail()).thenReturn("test@gmail.com");
        when(payload.get("given_name")).thenReturn(null);
        when(payload.get("family_name")).thenReturn("Last");
        when(payload.get("picture")).thenReturn("http://pic.url");
        when(payload.get("name")).thenReturn("Full Name");

        boolean result = authenticateService.googleAuth(request);

        assertTrue(result);
        verify(userService).createGoogleUser(eq("test@gmail.com"), eq("Full Name"), eq("Last"), eq("http://pic.url"));
    }

    @Test
    void googleAuth_InvalidToken() throws GeneralSecurityException, IOException {
        GoogleAuthRequest request = new GoogleAuthRequest("invalidToken", "test@gmail.com");

        GoogleIdTokenVerifier verifier = mock(GoogleIdTokenVerifier.class);

        doReturn(verifier).when(authenticateService).getVerifier();
        when(verifier.verify(anyString())).thenReturn(null);

        boolean result = authenticateService.googleAuth(request);

        assertFalse(result);
        verify(userService, never()).createGoogleUser(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void googleAuth_Exception() throws GeneralSecurityException, IOException {
        GoogleAuthRequest request = new GoogleAuthRequest("token", "test@gmail.com");

        GoogleIdTokenVerifier verifier = mock(GoogleIdTokenVerifier.class);

        doReturn(verifier).when(authenticateService).getVerifier();
        when(verifier.verify(anyString())).thenThrow(new IOException("Network error"));

        boolean result = authenticateService.googleAuth(request);

        assertFalse(result);
    }
}
