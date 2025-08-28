package com.tqt.englishApp.controller;

import com.tqt.englishApp.dto.request.ApiResponse;
import com.tqt.englishApp.dto.request.MainTopicRequest;
import com.tqt.englishApp.dto.request.UserCreationRequest;
import com.tqt.englishApp.dto.request.UserUpdateRequest;
import com.tqt.englishApp.dto.response.MainTopicResponse;
import com.tqt.englishApp.dto.response.UserResponse;
import com.tqt.englishApp.service.MainTopicService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiMainTopicController {
    @Autowired
    private MainTopicService mainTopicService;

    @PostMapping(path = "/main-topics", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<MainTopicResponse> createMainTopic(@ModelAttribute @Valid MainTopicRequest topic) {
        ApiResponse<MainTopicResponse> response = new ApiResponse<>();
        response.setResult(mainTopicService.createMainTopic(topic));
        return response;
    }

    @GetMapping("/main-topics")
    public ApiResponse<Page<MainTopicResponse>> getMainTopics(@RequestParam Map<String, String> params) {
        ApiResponse<Page<MainTopicResponse>> response = new ApiResponse<>();
        response.setResult(mainTopicService.getMainTopics(params));
        return response;
    }

    @GetMapping("/main-topics/{id}")
    public ApiResponse<MainTopicResponse> getMainTopic(@PathVariable("id") Integer id) {
        ApiResponse<MainTopicResponse> response = new ApiResponse<>();
        response.setResult(mainTopicService.getMainTopicById(id));
        return response;
    }

    @PutMapping(path="/main-topics/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<MainTopicResponse> updateMainTopic(@ModelAttribute MainTopicRequest request, @PathVariable("id") Integer id) {
        ApiResponse<MainTopicResponse> response = new ApiResponse<>();
        response.setResult(mainTopicService.updateMainTopic(id, request));
        return response;
    }

    @DeleteMapping("/main-topics/{id}")
    public ApiResponse<MainTopicResponse> deleteMainTopic(@PathVariable Integer id) {
        ApiResponse<MainTopicResponse> response = new ApiResponse<>();
        mainTopicService.deleteMainTopic(id);
        response.setMessage("Delete main Topic Sucessfully");
        return response;
    }
}
