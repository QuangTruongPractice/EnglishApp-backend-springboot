package com.tqt.englishApp.controller;

import com.tqt.englishApp.dto.request.*;
import com.tqt.englishApp.dto.response.UserResponse;
import com.tqt.englishApp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiUserController {
    @Autowired
    private UserService userService;

    @PostMapping("/reset-password")
    public ApiResponse<String> resetPassword(@RequestBody @Valid ResetPasswordRequest resetPasswordRequest) {
        ApiResponse<String> response = new ApiResponse<>();
        userService.resetPassword(resetPasswordRequest);
        response.setMessage("Gửi yêu cầu thành công");
        return response;
    }

    @PostMapping("/verified-otp")
    public ApiResponse<String> optVerified(@RequestBody @Valid OtpVerifiedRequest request) {
        ApiResponse<String> response = new ApiResponse<>();
        String msg = userService.optVerifiedRequest(request);
        response.setMessage(msg);
        return response;
    }

    @PostMapping("/change-password")
    public ApiResponse<UserResponse> changePassword(@RequestBody @Valid ChangePasswordRequest changePasswordRequest) {
        ApiResponse<UserResponse> response = new ApiResponse<>();
        response.setResult(userService.changePassword(changePasswordRequest));
        return response;
    }

    @PostMapping(path = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<UserResponse> createUser(@ModelAttribute @Valid UserCreationRequest user) {
        ApiResponse<UserResponse> response = new ApiResponse<>();
        response.setResult(userService.createUser(user));
        return response;
    }
}
