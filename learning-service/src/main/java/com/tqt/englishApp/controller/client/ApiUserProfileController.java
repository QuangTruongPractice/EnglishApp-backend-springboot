package com.tqt.englishApp.controller.client;

import com.tqt.englishApp.dto.request.ApiResponse;
import com.tqt.englishApp.dto.request.UserProfileRequest;
import com.tqt.englishApp.dto.response.UserLearningProfileResponse;
import com.tqt.englishApp.mapper.UserLearningProfileMapper;
import com.tqt.englishApp.service.UserLearningProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiUserProfileController {
    @Autowired
    private UserLearningProfileService userLearningProfileService;

    @Autowired
    private UserLearningProfileMapper userLearningProfileMapper;

    @GetMapping("/secure/learning-profile")
    public ApiResponse<UserLearningProfileResponse> getProfile(Principal principal) {
        ApiResponse<UserLearningProfileResponse> response = new ApiResponse<>();
        response.setResult(userLearningProfileMapper.toResponse(userLearningProfileService.getProfile(principal.getName())));
        return response;
    }

    @PostMapping("/secure/learning-profile")
    public ApiResponse<UserLearningProfileResponse> createProfile(
            Principal principal,
            @RequestBody UserProfileRequest request) {
        ApiResponse<UserLearningProfileResponse> response = new ApiResponse<>();
        response.setResult(userLearningProfileMapper.toResponse(userLearningProfileService.createOrUpdateProfile(principal.getName(), request)));
        return response;
    }

    @PutMapping("/secure/learning-profile")
    public ApiResponse<UserLearningProfileResponse> updateProfile(
            Principal principal,
            @RequestBody UserProfileRequest request) {
        ApiResponse<UserLearningProfileResponse> response = new ApiResponse<>();
        response.setResult(userLearningProfileMapper.toResponse(userLearningProfileService.updateProfile(principal.getName(), request)));
        return response;
    }
}
