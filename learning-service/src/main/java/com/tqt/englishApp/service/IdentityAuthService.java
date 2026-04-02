package com.tqt.englishApp.service;

import com.tqt.englishApp.dto.request.ApiResponse;
import com.tqt.englishApp.dto.request.AuthenticationRequest;
import com.tqt.englishApp.dto.response.AuthenticationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class IdentityAuthService {

    @Autowired
    private RestTemplate restTemplate;

    private static final String IDENTITY_SERVICE_URL = "http://localhost:8080/identity/api/login";

    public AuthenticationResponse authenticate(String username, String password) {
        AuthenticationRequest request = new AuthenticationRequest(username, password);
        HttpEntity<AuthenticationRequest> entity = new HttpEntity<>(request);

        try {
            ResponseEntity<ApiResponse<AuthenticationResponse>> response = restTemplate.exchange(
                    IDENTITY_SERVICE_URL,
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<ApiResponse<AuthenticationResponse>>() {
                    });

            if (response.getBody() != null && response.getBody().getResult() != null) {
                return response.getBody().getResult();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
