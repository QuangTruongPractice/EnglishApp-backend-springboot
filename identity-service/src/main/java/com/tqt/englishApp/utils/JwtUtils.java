package com.tqt.englishApp.utils;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtils {
    private static final String SECRET = "12345678901234567890123456789012";
    private static final long EXPIRATION_MS = 1800000; // 30 minutes
    private static final long REFRESH_EXPIRATION_MS = 604800000; // 7 days

    public static String generateToken(String username, String role) throws Exception {
        return generateToken(username, role, EXPIRATION_MS);
    }

    public static String generateRefreshToken(String username, String role) throws Exception {
        return generateToken(username, role, REFRESH_EXPIRATION_MS);
    }

    private static String generateToken(String username, String role, long expirationTime) throws Exception {
        JWSSigner signer = new MACSigner(SECRET);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(username)
                .claim("role", role)
                .expirationTime(new Date(System.currentTimeMillis() + expirationTime))
                .issueTime(new Date())
                .build();

        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader(JWSAlgorithm.HS256),
                claimsSet
        );

        signedJWT.sign(signer);

        return signedJWT.serialize();
    }

    public static Map<String, Object> validateTokenAndGetClaims(String token) throws Exception {
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWSVerifier verifier = new MACVerifier(SECRET);

        if (signedJWT.verify(verifier)) {
            Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();
            if (expiration.after(new Date())) {
                Map<String, Object> claims = new HashMap<>();
                claims.put("username",signedJWT.getJWTClaimsSet().getSubject());
                claims.put("role", signedJWT.getJWTClaimsSet().getStringClaim("role"));
                return claims;
            }
        }
        return null;
    }
}
