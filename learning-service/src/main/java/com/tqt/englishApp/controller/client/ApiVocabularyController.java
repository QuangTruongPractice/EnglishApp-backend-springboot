package com.tqt.englishApp.controller.client;

import com.tqt.englishApp.dto.request.ApiResponse;
import com.tqt.englishApp.dto.response.vocabulary.VocabulariesResponse;
import com.tqt.englishApp.dto.response.vocabulary.VocabulariesSimpleResponse;
import com.tqt.englishApp.service.VocabularyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiVocabularyController {
    @Autowired
    private VocabularyService vocabularyService;

    @GetMapping("/secure/vocabulary/save")
    public ApiResponse<Page<VocabulariesSimpleResponse>> getSaveVocabularies(
            @RequestParam Map<String, String> params, 
            Principal principal) {
        ApiResponse<Page<VocabulariesSimpleResponse>> response = new ApiResponse<>();
        response.setResult(vocabularyService.getSaveVocabularies(principal.getName(), params));
        return response;
    }

    @GetMapping("/secure/vocabulary/{id}")
    public ApiResponse<VocabulariesResponse> getVocabulary(
            @PathVariable("id") Integer id, 
            Principal principal) {
        ApiResponse<VocabulariesResponse> response = new ApiResponse<>();
        String userId = (principal != null) ? principal.getName() : null;
        response.setResult(vocabularyService.getVocabularyById(id, userId));
        return response;
    }

    @PostMapping("/secure/vocabulary/{id}/toggle")
    public ApiResponse<Void> toggleSaveVocabulary(
            @PathVariable("id") Integer id, 
            Principal principal) {
        ApiResponse<Void> response = new ApiResponse<>();
        vocabularyService.toggleSaveVocabulary(id, principal.getName());
        response.setMessage("Toggle save vocabulary successfully");
        return response;
    }
}
