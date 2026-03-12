package com.tqt.englishApp.controller.client;

import com.tqt.englishApp.dto.request.ApiResponse;
import com.tqt.englishApp.dto.response.mainTopic.MainTopicsDetailResponse;
import com.tqt.englishApp.dto.response.mainTopic.MainTopicsResponse;
import com.tqt.englishApp.service.MainTopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiMainTopicController {
    @Autowired
    private MainTopicService mainTopicService;

    @GetMapping("/secure/learning-path")
    public ApiResponse<List<MainTopicsResponse>> getLearningPath(Principal principal) {
        ApiResponse<List<MainTopicsResponse>> response = new ApiResponse<>();
        response.setResult(mainTopicService.getLearningPathForClient(principal.getName()));
        return response;
    }

    @GetMapping("/main-topics")
    public ApiResponse<Page<MainTopicsResponse>> getMainTopics(@RequestParam Map<String, String> params) {
        ApiResponse<Page<MainTopicsResponse>> response = new ApiResponse<>();
        response.setResult(mainTopicService.getMainTopicsForClient(params));
        return response;
    }

    @GetMapping("/main-topics/{id}")
    public ApiResponse<MainTopicsDetailResponse> getMainTopicDetail(@PathVariable("id") Integer id) {
        ApiResponse<MainTopicsDetailResponse> response = new ApiResponse<>();
        response.setResult(mainTopicService.getMainTopicDetailForClient(id));
        return response;
    }

}
