package com.tqt.englishApp.controller.client;

import com.tqt.englishApp.dto.request.ApiResponse;
import com.tqt.englishApp.dto.response.WeeklyLeaderboardResponse;
import com.tqt.englishApp.entity.UserLearningProfile;
import com.tqt.englishApp.repository.UserLearningProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiLeaderBoardController {
    private final UserLearningProfileRepository profileRepository;

    @GetMapping("/leaderboard/weekly")
    public ApiResponse<List<WeeklyLeaderboardResponse>> getWeeklyLeaderboard() {
        List<UserLearningProfile> topUsers = profileRepository.findTop10ByWeeklyXp();
        List<WeeklyLeaderboardResponse> responseList = new ArrayList<>();
        
        for (int i = 0; i < topUsers.size(); i++) {
            UserLearningProfile user = topUsers.get(i);
            responseList.add(WeeklyLeaderboardResponse.builder()
                    .userId(user.getUserId())
                    .username("User " + user.getUserId()) 
                    .weeklyXp(user.getWeeklyXp())
                    .rank(i + 1)
                    .level(user.getLevel().name())
                    .build());
        }

        ApiResponse<List<WeeklyLeaderboardResponse>> response = new ApiResponse<>();
        response.setResult(responseList);
        return response;
    }
}
