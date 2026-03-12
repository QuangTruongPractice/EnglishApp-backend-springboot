package com.tqt.englishApp.controller.client;

import com.tqt.englishApp.dto.request.ApiResponse;
import com.tqt.englishApp.dto.request.UserProfileRequest;
import com.tqt.englishApp.entity.UserLearningProfile;
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

    @GetMapping("/secure/profile")
    public ApiResponse<UserLearningProfile> getProfile(Principal principal) {
        ApiResponse<UserLearningProfile> response = new ApiResponse<>();
        response.setResult(userLearningProfileService.getProfile(principal.getName()));
        return response;
    }

    @PostMapping("/secure/profile")
    public ApiResponse<UserLearningProfile> createProfile(
            Principal principal,
            @RequestBody UserProfileRequest request) {
        ApiResponse<UserLearningProfile> response = new ApiResponse<>();

        String userId = principal.getName();
        UserLearningProfile profile = userLearningProfileService.createOrUpdateProfile(userId, request);

        response.setResult(profile);
        return response;
    }
}
