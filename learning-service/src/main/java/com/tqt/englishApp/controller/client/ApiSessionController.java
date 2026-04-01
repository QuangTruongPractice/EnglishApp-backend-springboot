package com.tqt.englishApp.controller.client;

import com.tqt.englishApp.dto.request.ApiResponse;
import com.tqt.englishApp.entity.Session;
import com.tqt.englishApp.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiSessionController {
    private final SessionService sessionService;

    @PostMapping("/secure/sessions/daily")
    public ApiResponse<Session> getOrCreateDailySession(Principal principal) {
        ApiResponse<Session> response = new ApiResponse<>();
        response.setResult(sessionService.getOrCreateDailySession(principal.getName()));
        return response;
    }

    @PostMapping("/secure/sessions/{sessionId}/quiz/{quizId}/submit")
    public ApiResponse<Integer> submitQuiz(@PathVariable Integer sessionId,
                                           @PathVariable Integer quizId,
                                           @RequestBody Boolean isCorrect,
                                           Principal principal) {
        ApiResponse<Integer> response = new ApiResponse<>();
        response.setResult(sessionService.submitQuiz(sessionId, quizId, principal.getName(), isCorrect));
        return response;
    }

    @PostMapping("/secure/sessions/{sessionId}/writing/submit")
    public ApiResponse<Integer> submitWriting(@PathVariable Integer sessionId,
                                              Principal principal) {
        ApiResponse<Integer> response = new ApiResponse<>();
        response.setResult(sessionService.submitWriting(sessionId, principal.getName()));
        return response;
    }

    @GetMapping("/sessions/{sessionId}/levelup-check")
    public ApiResponse<Boolean> checkLevelUp(@PathVariable Integer sessionId) {
        ApiResponse<Boolean> response = new ApiResponse<>();
        response.setResult(sessionService.checkLevelUp(sessionId));
        return response;
    }
}
