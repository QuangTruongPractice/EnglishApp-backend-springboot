package com.tqt.englishApp.configs;

import com.tqt.englishApp.dto.response.AuthenticationResponse;
import com.tqt.englishApp.service.IdentityClient;
import com.tqt.englishApp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Arrays;

@Component
public class IdentityAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private IdentityClient identityClient;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        AuthenticationResponse response = identityClient.authenticate(username, password);

        if (response != null && response.isAuthenticated()) {
            try {
                Map<String, Object> claims = JwtUtils.validateTokenAndGetClaims(response.getToken());
                if (claims != null) {
                    String roleStr = (String) claims.get("role");
                    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    if (roleStr != null) {
                        authorities = Arrays.stream(roleStr.split(" "))
                                .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                                .collect(Collectors.toList());
                    }

                    return new UsernamePasswordAuthenticationToken(username, password, authorities);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        throw new UsernameNotFoundException("Invalid username or password");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
