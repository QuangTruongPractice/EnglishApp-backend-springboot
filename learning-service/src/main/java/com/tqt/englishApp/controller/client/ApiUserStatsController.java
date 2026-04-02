package com.tqt.englishApp.controller.client;

import com.tqt.englishApp.dto.request.ApiResponse;
import com.tqt.englishApp.dto.response.LearningSummaryResponse;
import com.tqt.englishApp.dto.response.StreakCalendarResponse;
import com.tqt.englishApp.service.UserStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/secure/stats")
@CrossOrigin
public class ApiUserStatsController {

    @Autowired
    private UserStatsService userStatsService;

    @GetMapping("/streak-calendar")
    public ApiResponse<StreakCalendarResponse> getStreakCalendar(
            Principal principal,
            @RequestParam int month,
            @RequestParam int year) {
        ApiResponse<StreakCalendarResponse> response = new ApiResponse<>();
        String userId = principal.getName();
        StreakCalendarResponse calendar = userStatsService.getStreakCalendar(userId, month, year);
        response.setResult(calendar);
        return response;
    }

    @GetMapping("/summary")
    public ApiResponse<LearningSummaryResponse> getLearningSummary(Principal principal) {
        ApiResponse<LearningSummaryResponse> response = new ApiResponse<>();
        String userId = principal.getName();
        LearningSummaryResponse summary = userStatsService.getLearningSummary(userId);
        response.setResult(summary);
        return response;
    }
}
