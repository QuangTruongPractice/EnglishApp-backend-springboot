package com.tqt.englishApp.controller.client;

import com.tqt.englishApp.dto.request.ApiResponse;
import com.tqt.englishApp.dto.response.AiAnalysisResponse;
import com.tqt.englishApp.dto.response.SessionResponse;
import com.tqt.englishApp.dto.response.SubmitQuizResponse;
import com.tqt.englishApp.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiSessionController {

    private final SessionService sessionService;

    @PostMapping("/secure/sessions/daily")
    public ApiResponse<SessionResponse> createSession(Principal principal) {
        ApiResponse<SessionResponse> response = new ApiResponse<>();
        response.setResult(sessionService.getOrCreateSession(principal.getName()));
        return response;
    }

    @PostMapping("/secure/sessions/{sessionId}/quiz/{sessionQuizId}/submit")
    public ApiResponse<SubmitQuizResponse> submitQuiz(@PathVariable Integer sessionId,
                                           @PathVariable Integer sessionQuizId,
                                           @RequestParam Boolean isCorrect,
                                           @RequestParam(required = false) Long responseTime,
                                           Principal principal) {
        ApiResponse<SubmitQuizResponse> response = new ApiResponse<>();
        response.setResult(sessionService.submitQuiz(sessionId, sessionQuizId, principal.getName(), isCorrect, responseTime));
        return response;
    }

    @PostMapping("/secure/sessions/{sessionId}/writing/{promptId}/submit")
    public ApiResponse<AiAnalysisResponse> submitWriting(@PathVariable Integer sessionId,
                                                         @PathVariable Integer promptId,
                                                         @RequestBody Map<String, String> body,
                                                         Principal principal) {
        ApiResponse<AiAnalysisResponse> response = new ApiResponse<>();
        response.setResult(sessionService.submitWriting(sessionId, promptId, principal.getName(), body.get("text")));
        return response;
    }

    @GetMapping("/sessions/{sessionId}/levelup-check")
    public ApiResponse<Boolean> checkLevelUp(@PathVariable Integer sessionId) {
        ApiResponse<Boolean> response = new ApiResponse<>();
        response.setResult(sessionService.checkLevelUp(sessionId));
        return response;
    }
}
