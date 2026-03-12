package com.tqt.englishApp.controller.client;

import com.tqt.englishApp.dto.request.ApiResponse;
import com.tqt.englishApp.dto.response.subTopic.SubTopicsDetailResponse;
import com.tqt.englishApp.service.SubTopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiSubTopicController {
    @Autowired
    private SubTopicService subTopicService;

    @GetMapping("/sub-topics/{id}")
    public ApiResponse<SubTopicsDetailResponse> getSubTopic(@PathVariable("id") Integer id) {
        ApiResponse<SubTopicsDetailResponse> response = new ApiResponse<>();
        response.setResult(subTopicService.getSubTopicDetailForClient(id));
        return response;
    }
}
