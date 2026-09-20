package com.tqt.englishApp.controller.client;

import com.tqt.englishApp.dto.request.ApiResponse;
import com.tqt.englishApp.dto.response.UserIdentityResponse;
import com.tqt.englishApp.dto.response.WeeklyLeaderboardResponse;
import com.tqt.englishApp.dto.response.WeeklyLeaderboardWrapperResponse;
import com.tqt.englishApp.entity.UserLearningProfile;
import com.tqt.englishApp.repository.UserLearningProfileRepository;
import com.tqt.englishApp.service.IdentityClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiLeaderBoardController {
    private final UserLearningProfileRepository profileRepository;
    private final IdentityClient identityClient;

    @GetMapping("/secure/leaderboard/weekly")
    public ApiResponse<WeeklyLeaderboardWrapperResponse> getSecureWeeklyLeaderboard(Principal principal) {
        // Fetch top 10 profiles
        List<UserLearningProfile> topUsers = profileRepository.findTop10ByWeeklyXp();
        
        List<String> usernames = topUsers.stream()
                .map(UserLearningProfile::getUserId)
                .collect(Collectors.toList());

        String currentUserId = principal.getName();
        if (!usernames.contains(currentUserId)) {
            usernames.add(currentUserId);
        }

        List<UserIdentityResponse> identities = identityClient.getUsersByUsernames(usernames);
        Map<String, UserIdentityResponse> userMap = identities.stream()
                .collect(Collectors.toMap(UserIdentityResponse::getUsername, u -> u, (u1, u2) -> u1));

        List<WeeklyLeaderboardResponse> responseList = new ArrayList<>();
        int currentRank = 1;
        
        for (int i = 0; i < topUsers.size(); i++) {
            UserLearningProfile user = topUsers.get(i);
            UserIdentityResponse identity = userMap.get(user.getUserId());
            String fullName = identity != null ? identity.getFullName() : "Unknown User";
            String avatar = identity != null ? identity.getAvatar() : null;
            String frameAvatar = identity != null ? identity.getEquippedFrameKey() : null;
            
            if (i > 0 && user.getWeeklyXp() < topUsers.get(i - 1).getWeeklyXp()) {
                currentRank = i + 1;
            }
            
            responseList.add(WeeklyLeaderboardResponse.builder()
                    .userId(user.getUserId())
                    .username(fullName) 
                    .weeklyXp(user.getWeeklyXp())
                    .rank(currentRank)
                    .level(user.getLevel().name())
                    .avatar(avatar)
                    .frameAvatar(frameAvatar)
                    .equippedFrameKey(frameAvatar)
                    .build());
        }

        UserLearningProfile currentUserProfile = profileRepository.findByUserId(currentUserId).orElse(null);
        WeeklyLeaderboardResponse currentUserResponse = null;
        if (currentUserProfile != null) {
            UserIdentityResponse identity = userMap.get(currentUserId);
            String fullName = identity != null ? identity.getFullName() : "Unknown User";
            String avatar = identity != null ? identity.getAvatar() : null;
            String frameAvatar = identity != null ? identity.getEquippedFrameKey() : null;
            int rank = profileRepository.getWeeklyRank(currentUserId);
            
            currentUserResponse = WeeklyLeaderboardResponse.builder()
                    .userId(currentUserId)
                    .username(fullName)
                    .weeklyXp(currentUserProfile.getWeeklyXp())
                    .rank(rank)
                    .level(currentUserProfile.getLevel().name())
                    .avatar(avatar)
                    .frameAvatar(frameAvatar)
                    .equippedFrameKey(frameAvatar)
                    .build();
        }

        WeeklyLeaderboardWrapperResponse wrapper = new WeeklyLeaderboardWrapperResponse(responseList, currentUserResponse);
        ApiResponse<WeeklyLeaderboardWrapperResponse> response = new ApiResponse<>();
        response.setResult(wrapper);
        return response;
    }
}
