package com.tqt.englishApp.controller;

import com.tqt.englishApp.dto.request.ApiResponse;
import com.tqt.englishApp.dto.request.SubTopicRequest;
import com.tqt.englishApp.dto.response.SubTopicResponse;
import com.tqt.englishApp.service.SubTopicService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiSubTopicController {
    @Autowired
    private SubTopicService subTopicService;

    @PostMapping(path = "/sub-topics", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<SubTopicResponse> createSubTopic(@ModelAttribute @Valid SubTopicRequest topic) {
        ApiResponse<SubTopicResponse> response = new ApiResponse<>();
        response.setResult(subTopicService.createSubTopic(topic));
        return response;
    }

    @GetMapping("/sub-topics")
    public ApiResponse<Page<SubTopicResponse>> getSubTopics(@RequestParam Map<String, String> params) {
        ApiResponse<Page<SubTopicResponse>> response = new ApiResponse<>();
        response.setResult(subTopicService.getSubTopics(params));
        return response;
    }

    @GetMapping("/sub-topics/{id}")
    public ApiResponse<SubTopicResponse> getSubTopic(@PathVariable("id") Integer id) {
        ApiResponse<SubTopicResponse> response = new ApiResponse<>();
        response.setResult(subTopicService.getSubTopicById(id));
        return response;
    }

    @PutMapping(path="/sub-topics/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<SubTopicResponse> updateSubTopic(@ModelAttribute SubTopicRequest request, @PathVariable("id") Integer id) {
        ApiResponse<SubTopicResponse> response = new ApiResponse<>();
        response.setResult(subTopicService.updateSubTopic(id, request));
        return response;
    }

    @DeleteMapping("/sub-topics/{id}")
    public ApiResponse<SubTopicResponse> deleteSubTopic(@PathVariable Integer id) {
        ApiResponse<SubTopicResponse> response = new ApiResponse<>();
        subTopicService.deleteSubTopic(id);
        response.setMessage("Delete sub Topic Sucessfully");
        return response;
    }
}
