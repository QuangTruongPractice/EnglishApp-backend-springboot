package com.tqt.englishApp.controller.client;

import com.tqt.englishApp.dto.request.ApiResponse;
import com.tqt.englishApp.dto.response.LeaderBoardWrapperResponse;
import com.tqt.englishApp.service.RankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiLeaderBoardController {
    @Autowired
    private RankingService rankingService;

    @GetMapping("/secure/leader-board")
    public ApiResponse<LeaderBoardWrapperResponse> getUserVocabularyProgress(Principal principal) {
        ApiResponse<LeaderBoardWrapperResponse> response = new ApiResponse<>();
        String userId = principal.getName();
        LeaderBoardWrapperResponse result = rankingService.getLeaderBoardWithCurrentUser(userId);
        response.setResult(result);
        return response;
    }
}
