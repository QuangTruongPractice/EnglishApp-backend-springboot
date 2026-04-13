package com.tqt.englishApp.service;

import com.tqt.englishApp.dto.request.ApiResponse;
import com.tqt.englishApp.dto.request.AuthenticationRequest;
import com.tqt.englishApp.dto.response.AuthenticationResponse;
import com.tqt.englishApp.dto.response.UserIdentityResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class IdentityClient {
    private final RestTemplate restTemplate;

    @Value("${services.identity.url}")
    private String identityServiceBaseUrl;

    public AuthenticationResponse authenticate(String username, String password) {
        AuthenticationRequest request = new AuthenticationRequest(username, password);
        HttpEntity<AuthenticationRequest> entity = new HttpEntity<>(request);
        String url = identityServiceBaseUrl + "/api/login";

        try {
            ResponseEntity<ApiResponse<AuthenticationResponse>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<ApiResponse<AuthenticationResponse>>() {
                    });

            if (response.getBody() != null && response.getBody().getResult() != null) {
                return response.getBody().getResult();
            }
        } catch (Exception e) {
            log.error("Error calling identity service for authentication: {}", e.getMessage());
        }
        return null;
    }

    public List<UserIdentityResponse> getUsersByUsernames(List<String> usernames) {
        if (usernames == null || usernames.isEmpty()) {
            return Collections.emptyList();
        }

        HttpEntity<List<String>> entity = new HttpEntity<>(usernames);
        String url = identityServiceBaseUrl + "/api/list";

        try {
            ResponseEntity<ApiResponse<List<UserIdentityResponse>>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<ApiResponse<List<UserIdentityResponse>>>() {
                    });

            if (response.getBody() != null && response.getBody().getResult() != null) {
                return response.getBody().getResult();
            }
        } catch (Exception e) {
            log.error("Error calling identity service to fetch users: {}", e.getMessage());
        }
        return Collections.emptyList();
    }
}
