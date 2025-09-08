package com.tqt.englishApp.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.tqt.englishApp.dto.request.AuthenticationRequest;
import com.tqt.englishApp.dto.request.GoogleAuthRequest;
import com.tqt.englishApp.entity.User;
import com.tqt.englishApp.enums.Role;
import com.tqt.englishApp.exception.AppException;
import com.tqt.englishApp.exception.ErrorCode;
import com.tqt.englishApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class AuthenticateService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public boolean authentication(AuthenticationRequest authenticationRequest) {
        User user = userRepository.findUserByUsername(authenticationRequest.getUsername());
        if (user == null) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        boolean result = passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword());
        if(!result) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        return result;
    }

    public boolean googleAuth(GoogleAuthRequest googleAuthRequest) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), new JacksonFactory()
            ).setAudience(Collections.singletonList("395593925572-n0k16v2srue48kotcgi60rlmk0350sp9.apps.googleusercontent.com"))
                    .build();

            GoogleIdToken idToken = verifier.verify(googleAuthRequest.getIdToken());

            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();

                String email = payload.getEmail();
                String firstName = (String) payload.get("given_name");
                String lastName = (String) payload.get("family_name");
                String fullName = (String) payload.get("name");
                String pictureUrl = (String) payload.get("picture");

                User user = userRepository.findUserByEmail(email);
                if (user == null) {
                    user = new User();
                    user.setUsername(email);
                    user.setEmail(email);
                    user.setFirstName(firstName != null ? firstName : fullName);
                    user.setLastName(lastName);
                    user.setAvatar(pictureUrl);
                    user.setIsActive(true);
                    user.setRole(Role.USER);
                    userRepository.save(user);
                }
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
