package com.tqt.englishApp.utils;

import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    @Test
    void generateToken_AndValidate_Success() {
        String username = "testuser";
        String role = "USER_ROLE";
        
        String token = JwtUtils.generateToken(username, role);
        assertNotNull(token);
        
        Map<String, Object> claims = JwtUtils.validateTokenAndGetClaims(token);
        assertNotNull(claims);
        assertEquals(username, claims.get("sub"));
        assertEquals(role, claims.get("role"));
    }

    @Test
    void validateToken_InvalidToken_ReturnsNull() {
        assertNull(JwtUtils.validateTokenAndGetClaims("invalid-token-string"));
        assertNull(JwtUtils.validateTokenAndGetClaims(null));
    }

    @Test
    void validateToken_ExpiredOrTamperedToken_ReturnsNull() {
        // Tamper with a valid token
        String token = JwtUtils.generateToken("user", "ROLE");
        String tamperedToken = token.substring(0, token.length() - 5) + "abcde";
        
        assertNull(JwtUtils.validateTokenAndGetClaims(tamperedToken));
    }
}
