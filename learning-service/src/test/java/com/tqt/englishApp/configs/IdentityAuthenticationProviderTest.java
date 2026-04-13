package com.tqt.englishApp.configs;

import com.tqt.englishApp.dto.response.AuthenticationResponse;
import com.tqt.englishApp.service.IdentityClient;
import com.tqt.englishApp.utils.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IdentityAuthenticationProviderTest {

    @Mock
    private IdentityClient identityClient;

    @InjectMocks
    private IdentityAuthenticationProvider provider;

    private String validToken;

    @BeforeEach
    void setUp() throws Exception {
        validToken = JwtUtils.generateToken("admin", "ADMIN STAFF");
    }

    @Test
    void authenticate_Success() {
        AuthenticationResponse response = new AuthenticationResponse(true, validToken);
        when(identityClient.authenticate("admin", "password")).thenReturn(response);

        Authentication authRequest = new UsernamePasswordAuthenticationToken("admin", "password");
        Authentication result = provider.authenticate(authRequest);

        assertNotNull(result);
        assertTrue(result.isAuthenticated());
        assertEquals("admin", result.getName());
        assertTrue(result.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
        assertTrue(result.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_STAFF")));
    }

    @Test
    void authenticate_InvalidCredentials_ThrowsException() {
        AuthenticationResponse response = new AuthenticationResponse(false, null);
        when(identityClient.authenticate("wrong", "pass")).thenReturn(response);

        Authentication authRequest = new UsernamePasswordAuthenticationToken("wrong", "pass");

        assertThrows(UsernameNotFoundException.class, () -> provider.authenticate(authRequest));
    }

    @Test
    void authenticate_InvalidToken_ThrowsException() {
        AuthenticationResponse response = new AuthenticationResponse(true, "invalid-token");
        when(identityClient.authenticate("admin", "password")).thenReturn(response);

        Authentication authRequest = new UsernamePasswordAuthenticationToken("admin", "password");

        assertThrows(UsernameNotFoundException.class, () -> provider.authenticate(authRequest));
    }

    @Test
    void supports_Success() {
        assertTrue(provider.supports(UsernamePasswordAuthenticationToken.class));
        assertFalse(provider.supports(String.class));
    }
}
