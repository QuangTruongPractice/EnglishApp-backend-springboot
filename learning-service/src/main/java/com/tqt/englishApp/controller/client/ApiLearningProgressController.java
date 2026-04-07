package com.tqt.englishApp.controller.client;

import com.tqt.englishApp.dto.request.ApiResponse;
import com.tqt.englishApp.dto.request.VideoProgressRequest;
import com.tqt.englishApp.dto.request.VocabularyProgressRequest;
import com.tqt.englishApp.dto.response.*;
import com.tqt.englishApp.dto.response.vocabulary.UserVocabularyResponse;
import com.tqt.englishApp.entity.*;
import com.tqt.englishApp.service.ContentProgressService;
import com.tqt.englishApp.service.VocabularyLearningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiLearningProgressController {
    @Autowired
    private VocabularyLearningService vocabularyLearningService;

    @Autowired
    private ContentProgressService contentProgressService;

    @PostMapping("/learning-progress/video")
    public ApiResponse<UserVideoProgress> updateVideoProgress(@RequestBody VideoProgressRequest request) {
        ApiResponse<UserVideoProgress> response = new ApiResponse<>();
        UserVideoProgress progress = contentProgressService.updateVideoProgress(request);
        response.setResult(progress);
        return response;
    }

    @GetMapping("/secure/learning-progress/vocabulary")
    public ApiResponse<Page<UserVocabularyResponse>> getUserVocabularyProgress(
            @RequestParam Map<String, String> params,
            Principal principal) {
        ApiResponse<Page<UserVocabularyResponse>> response = new ApiResponse<>();
        String userId = principal.getName();
        Page<UserVocabularyResponse> progress = vocabularyLearningService.getUserVocabularyProgress(userId, params);
        response.setResult(progress);
        return response;
    }

    @GetMapping("/secure/learning-progress/video")
    public ApiResponse<Page<UserVideoResponse>> getUserVideoProgress(
            @RequestParam Map<String, String> params,
            Principal principal) {
        ApiResponse<Page<UserVideoResponse>> response = new ApiResponse<>();
        String userId = principal.getName();
        Page<UserVideoResponse> progress = contentProgressService.getUserVideoProgress(userId, params);
        response.setResult(progress);
        return response;
    }

    @PutMapping("/secure/vocabulary/meaning/{meaningId}/quiz-result")
    public ApiResponse<UserVocabularyResponse> updateQuizResult(
            Principal principal,
            @PathVariable Integer meaningId,
            @RequestParam Boolean isCorrect) {
        ApiResponse<UserVocabularyResponse> response = new ApiResponse<>();
        String userId = principal.getName();
        VocabularyProgressRequest request = VocabularyProgressRequest.builder()
                .userId(userId)
                .meaningId(meaningId)
                .isCorrect(isCorrect)
                .build();

        UserVocabularyResponse updated = vocabularyLearningService.updateVocabularyProgress(request);
        response.setResult(updated);
        return response;
    }

    @PutMapping("/secure/video/{videoId}/update-progress")
    public ApiResponse<UserVideoProgress> updateVideoProgressByPath(
            Principal principal,
            @PathVariable Integer videoId,
            @RequestBody VideoProgressRequest request) {
        ApiResponse<UserVideoProgress> response = new ApiResponse<>();
        String userId = principal.getName();
        VideoProgressRequest progressRequest = VideoProgressRequest.builder()
                .userId(userId)
                .videoId(videoId)
                .watchedDuration(request.getWatchedDuration())
                .videoDuration(request.getVideoDuration())
                .lastPosition(request.getLastPosition())
                .build();

        UserVideoProgress progress = contentProgressService.updateVideoProgress(progressRequest);
        response.setResult(progress);
        return response;
    }

    @PutMapping("/secure/quiz/{quizId}/quiz-progress")
    public ApiResponse<UserQuizProgress> updateQuizProgress(Principal principal, @PathVariable Integer quizId) {
        ApiResponse<UserQuizProgress> response = new ApiResponse<>();
        String userId = principal.getName();
        UserQuizProgress progress = contentProgressService.updateQuizProgress(userId, quizId);
        response.setResult(progress);
        return response;
    }

}
