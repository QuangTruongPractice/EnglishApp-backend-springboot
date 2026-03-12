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
        profile.setOnboardingCompleted(true);

        return userLearningProfileRepository.save(profile);
    }
}
