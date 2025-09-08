package com.tqt.englishApp.controller;

import com.tqt.englishApp.dto.request.ApiResponse;
import com.tqt.englishApp.dto.response.LeaderBoardResponse;
import com.tqt.englishApp.dto.response.LeaderBoardWrapperResponse;
import com.tqt.englishApp.dto.response.UserResponse;
import com.tqt.englishApp.dto.response.UserVocabularyResponse;
import com.tqt.englishApp.service.LearningProgressService;
import com.tqt.englishApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiLeaderBoardController {
    @Autowired
    private LearningProgressService learningProgressService;

    @Autowired
    private UserService userService;

    @GetMapping("/secure/leader-board")
    public ApiResponse<LeaderBoardWrapperResponse> getUserVocabularyProgress(Principal principal) {
        ApiResponse<LeaderBoardWrapperResponse> response = new ApiResponse<>();
        String username = principal.getName();
        UserResponse user = userService.findUserByUsername(username);
        LeaderBoardWrapperResponse result = learningProgressService.getLeaderBoardWithCurrentUser(user.getId());
        response.setResult(result);
        return response;
    }
}
