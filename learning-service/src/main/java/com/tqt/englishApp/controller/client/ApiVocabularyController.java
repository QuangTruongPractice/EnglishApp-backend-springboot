package com.tqt.englishApp.controller.client;

import com.tqt.englishApp.dto.request.ApiResponse;
import com.tqt.englishApp.dto.response.vocabulary.VocabulariesResponse;
import com.tqt.englishApp.dto.response.vocabulary.VocabulariesSimpleResponse;
import com.tqt.englishApp.service.VocabularyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiVocabularyController {
    @Autowired
    private VocabularyService vocabularyService;

    @GetMapping("/vocabulary/save")
    public ApiResponse<Page<VocabulariesSimpleResponse>> getSaveVocabularies(@RequestParam Map<String, String> params) {
        ApiResponse<Page<VocabulariesSimpleResponse>> response = new ApiResponse<>();
        response.setResult(vocabularyService.getSaveVocabularies(params));
        return response;
    }

    @GetMapping("/vocabulary/{id}")
    public ApiResponse<VocabulariesResponse> getVocabulary(@PathVariable("id") Integer id) {
        ApiResponse<VocabulariesResponse> response = new ApiResponse<>();
        response.setResult(vocabularyService.getVocabularyById(id));
        return response;
    }

    @PostMapping("/vocabulary/{id}/toggle")
    public ApiResponse<VocabulariesResponse> toggleSaveVocabulary(@PathVariable("id") Integer id) {
        ApiResponse<VocabulariesResponse> response = new ApiResponse<>();
        vocabularyService.toggleSaveVocabulary(id);
        response.setMessage("Toggle save vocabulary Sucessfully");
        return response;
    }
}
