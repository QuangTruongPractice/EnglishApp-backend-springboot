package com.tqt.englishApp.controller.client;

import com.tqt.englishApp.dto.request.ApiResponse;
import com.tqt.englishApp.dto.request.VideoProgressRequest;
import com.tqt.englishApp.dto.request.VocabularyProgressRequest;
import com.tqt.englishApp.dto.response.*;
import com.tqt.englishApp.dto.response.vocabulary.VocabulariesResponse;
import com.tqt.englishApp.dto.response.vocabulary.UserVocabularyResponse;
import com.tqt.englishApp.entity.*;
import com.tqt.englishApp.mapper.VocabularyMapper;
import com.tqt.englishApp.service.ContentProgressService;
import com.tqt.englishApp.service.VocabularyLearningService;
import com.tqt.englishApp.service.VocabularySelectionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiLearningProgressController {
    @Autowired
    private VocabularyLearningService vocabularyLearningService;

    @Autowired
    private ContentProgressService contentProgressService;

    @Autowired
    private VocabularySelectionService vocabularySelectionService;

    @Autowired
    private VocabularyMapper vocabularyMapper;

    @PostMapping("/learning-progress/video")
    public ApiResponse<UserVideoProgress> updateVideoProgress(@RequestBody VideoProgressRequest request) {
        ApiResponse<UserVideoProgress> response = new ApiResponse<>();
        UserVideoProgress progress = contentProgressService.updateVideoProgress(request);
        response.setResult(progress);
        return response;
    }

    @GetMapping("/secure/learning-progress/vocabulary")
    public ApiResponse<List<UserVocabularyResponse>> getUserVocabularyProgress(Principal principal) {
        ApiResponse<List<UserVocabularyResponse>> response = new ApiResponse<>();
        String userId = principal.getName();
        List<UserVocabularyResponse> progress = vocabularyLearningService.getUserVocabularyProgress(userId);
        response.setResult(progress);
        return response;
    }

    @GetMapping("/secure/daily-vocabulary")
    public ApiResponse<List<DailyVocabularyResponse>> getDailyVocabulary(Principal principal) {
        ApiResponse<List<DailyVocabularyResponse>> response = new ApiResponse<>();
        String userId = principal.getName();
        List<DailyVocabularyItem> items = vocabularySelectionService.getDailyVocabulary(userId);
        response.setResult(items.stream()
                .map(item -> {
                    VocabulariesResponse base = vocabularyMapper.toVocabulariesResponse(item.getVocabulary());
                    DailyVocabularyResponse res = new DailyVocabularyResponse();
                    BeanUtils.copyProperties(base, res);
                    res.setIsReview(item.isReview());
                    res.setStatus(item.getStatus());
                    return res;
                })
                .collect(Collectors.toList()));
        return response;
    }

    @GetMapping("/secure/learning-progress/video")
    public ApiResponse<List<UserVideoResponse>> getUserVideoProgress(Principal principal) {
        ApiResponse<List<UserVideoResponse>> response = new ApiResponse<>();
        String userId = principal.getName();
        List<UserVideoResponse> progress = contentProgressService.getUserVideoProgress(userId);
        response.setResult(progress);
        return response;
    }

    @PutMapping("/secure/vocabulary/meaning/{meaningId}/quiz-result")
    public ApiResponse<UserVocabularyProgress> updateQuizResult(
            Principal principal,
            @PathVariable Integer meaningId,
            @RequestParam Boolean isCorrect) {
        ApiResponse<UserVocabularyProgress> response = new ApiResponse<>();
        String userId = principal.getName();
        VocabularyProgressRequest request = VocabularyProgressRequest.builder()
                .userId(userId)
                .meaningId(meaningId)
                .isCorrect(isCorrect)
                .build();

        UserVocabularyProgress updated = vocabularyLearningService.updateVocabularyProgress(request);
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
