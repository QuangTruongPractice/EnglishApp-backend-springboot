package com.tqt.englishApp.service;

import com.tqt.englishApp.dto.request.UserProfileRequest;
import com.tqt.englishApp.entity.UserLearningProfile;
import com.tqt.englishApp.repository.UserLearningProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserLearningProfileService {
    @Autowired
    private UserLearningProfileRepository userLearningProfileRepository;

    public UserLearningProfile getProfile(String userId) {
        return userLearningProfileRepository.findByUserId(userId)
                .orElse(null);
    }

    public UserLearningProfile createOrUpdateProfile(String userId, UserProfileRequest request) {
        Optional<UserLearningProfile> existingProfile = userLearningProfileRepository.findByUserId(userId);

        UserLearningProfile profile;
        if (existingProfile.isPresent()) {
            profile = existingProfile.get();
        } else {
            profile = new UserLearningProfile();
            profile.setUserId(userId);
        }

        profile.setLevel(request.getLevel());
        profile.setDailyTarget(request.getDailyTarget());
        profile.setGoal(request.getGoal());
        boolean isComplete = request.getLevel() != null &&
                            request.getDailyTarget() != null &&
                            request.getGoal() != null;
        profile.setOnboardingCompleted(isComplete);
        if (profile.getProfileUpdatedAt() == null) {
            profile.setProfileUpdatedAt(java.time.LocalDate.now());
        }

        return userLearningProfileRepository.save(profile);
    }

    public UserLearningProfile updateProfile(String userId, UserProfileRequest request) {
        UserLearningProfile profile = userLearningProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new com.tqt.englishApp.exception.AppException(com.tqt.englishApp.exception.ErrorCode.USER_NOT_EXISTED));

        if (profile.getProfileUpdatedAt() != null && profile.getProfileUpdatedAt().plusDays(7).isAfter(java.time.LocalDate.now())) {
            throw new com.tqt.englishApp.exception.AppException(com.tqt.englishApp.exception.ErrorCode.PROFILE_UPDATE_LIMITED);
        }

        if (request.getLevel() != null) profile.setLevel(request.getLevel());
        if (request.getDailyTarget() != null) profile.setDailyTarget(request.getDailyTarget());
        if (request.getGoal() != null) profile.setGoal(request.getGoal());
        
        boolean isComplete = profile.getLevel() != null &&
                            profile.getDailyTarget() != null &&
                            profile.getGoal() != null;
        profile.setOnboardingCompleted(isComplete);
        profile.setProfileUpdatedAt(java.time.LocalDate.now());

        return userLearningProfileRepository.save(profile);
    }
}
