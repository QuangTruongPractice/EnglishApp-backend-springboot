package com.tqt.englishApp.controller.client;

import com.tqt.englishApp.dto.request.ApiResponse;
import com.tqt.englishApp.dto.response.subTopic.SubTopicsDetailResponse;
import com.tqt.englishApp.service.SubTopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/secure")
@CrossOrigin
public class ApiSubTopicController {
    @Autowired
    private SubTopicService subTopicService;

    @GetMapping("/sub-topics/{id}")
    public ApiResponse<SubTopicsDetailResponse> getSubTopic(@PathVariable("id") Integer id, java.security.Principal principal) {
        ApiResponse<SubTopicsDetailResponse> response = new ApiResponse<>();
        String userId = principal != null ? principal.getName() : null;
        response.setResult(subTopicService.getSubTopicDetailForClient(id, userId));
        return response;
    }
}
