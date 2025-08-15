package com.tqt.englishApp.service;

import com.tqt.englishApp.dto.request.AuthenticationRequest;
import com.tqt.englishApp.entity.User;
import com.tqt.englishApp.exception.AppException;
import com.tqt.englishApp.exception.ErrorCode;
import com.tqt.englishApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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
}
