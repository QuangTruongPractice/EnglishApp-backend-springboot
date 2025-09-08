package com.tqt.englishApp.controller;

import com.tqt.englishApp.dto.request.ApiResponse;
import com.tqt.englishApp.dto.request.VocabularyRequest;
import com.tqt.englishApp.dto.response.VocabularyResponse;
import com.tqt.englishApp.service.VocabularyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiVocabularyController {
    @Autowired
    private VocabularyService vocabularyService;

    @PostMapping(path = "/vocabulary")
    public ApiResponse<VocabularyResponse> createVocabulary(@RequestBody @Valid VocabularyRequest topic) {
        ApiResponse<VocabularyResponse> response = new ApiResponse<>();
        response.setResult(vocabularyService.createVocabulary(topic));
        return response;
    }

    @GetMapping("/vocabulary")
    public ApiResponse<Page<VocabularyResponse>> getVocabularies(@RequestParam Map<String, String> params) {
        ApiResponse<Page<VocabularyResponse>> response = new ApiResponse<>();
        response.setResult(vocabularyService.getVocabularies(params));
        return response;
    }

    @GetMapping("/vocabulary/{id}")
    public ApiResponse<VocabularyResponse> getVocabulary(@PathVariable("id") Integer id) {
        ApiResponse<VocabularyResponse> response = new ApiResponse<>();
        response.setResult(vocabularyService.getVocabularyById(id));
        return response;
    }

    @PutMapping(path="/vocabulary/{id}")
    public ApiResponse<VocabularyResponse> updateVocabulary(@ModelAttribute VocabularyRequest request, @PathVariable("id") Integer id) {
        ApiResponse<VocabularyResponse> response = new ApiResponse<>();
        response.setResult(vocabularyService.updateVocabulary(id, request));
        return response;
    }

    @DeleteMapping("/vocabulary/{id}")
    public ApiResponse<VocabularyResponse> deleteVocabulary(@PathVariable Integer id) {
        ApiResponse<VocabularyResponse> response = new ApiResponse<>();
        vocabularyService.deleteVocabulary(id);
        response.setMessage("Delete vocabulary Sucessfully");
        return response;
    }
}
