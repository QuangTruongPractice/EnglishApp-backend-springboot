package com.tqt.englishApp.controller;

import com.tqt.englishApp.dto.request.ApiResponse;
import com.tqt.englishApp.dto.request.VideoProgressRequest;
import com.tqt.englishApp.dto.request.VocabularyProgressRequest;
import com.tqt.englishApp.dto.response.UserResponse;
import com.tqt.englishApp.dto.response.UserVideoResponse;
import com.tqt.englishApp.dto.response.UserVocabularyResponse;
import com.tqt.englishApp.entity.UserQuizProgress;
import com.tqt.englishApp.entity.UserVideoProgress;
import com.tqt.englishApp.entity.UserVocabularyProgress;
import com.tqt.englishApp.service.LearningProgressService;
import com.tqt.englishApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiLearningProgressController {
    @Autowired
    private LearningProgressService  learningProgressService;

    @Autowired
    private UserService userService;

    @PostMapping("/learning-progress/video")
    public ApiResponse<UserVideoProgress> updateVideoProgress(@RequestBody VideoProgressRequest request) {
        ApiResponse<UserVideoProgress> response = new ApiResponse<>();
        UserVideoProgress progress = learningProgressService.updateVideoProgress(request);
        response.setResult(progress);
        return response;
    }

    @GetMapping("/secure/learning-progress/vocabulary")
    public ApiResponse<List<UserVocabularyResponse>> getUserVocabularyProgress(Principal principal) {
        ApiResponse<List<UserVocabularyResponse>> response = new ApiResponse<>();
        String username = principal.getName();
        UserResponse user = userService.findUserByUsername(username);
        List<UserVocabularyResponse> progress = learningProgressService.getUserVocabularyProgress(user.getId());
        response.setResult(progress);
        return response;
    }

    @GetMapping("/secure/learning-progress/video")
    public ApiResponse<List<UserVideoResponse>> getUserVideoProgress(Principal principal) {
        ApiResponse<List<UserVideoResponse>> response = new ApiResponse<>();
        String username = principal.getName();
        UserResponse user = userService.findUserByUsername(username);
        List<UserVideoResponse> progress = learningProgressService.getUserVideoProgress(user.getId());
        response.setResult(progress);
        return response;
    }

    @PutMapping("/secure/vocabulary/{vocabularyId}/view-flashcard")
    public ApiResponse<UserVocabularyProgress> markFlashcardViewed(
            Principal principal,
            @PathVariable Integer vocabularyId) {
        ApiResponse<UserVocabularyProgress> response = new ApiResponse<>();
        String username = principal.getName();
        UserResponse user = userService.findUserByUsername(username);

        VocabularyProgressRequest request = VocabularyProgressRequest.builder()
                .userId(user.getId())
                .vocabularyId(vocabularyId)
                .viewedFlashcard(true)
                .build();

        UserVocabularyProgress updated = learningProgressService.updateVocabularyProgress(request);
        response.setResult(updated);
        return response;
    }

    @PutMapping("/secure/vocabulary/{vocabularyId}/practice-pronunciation")
    public ApiResponse<UserVocabularyProgress> markPronunciationPracticed(
            Principal principal,
            @PathVariable Integer vocabularyId) {
        ApiResponse<UserVocabularyProgress> response = new ApiResponse<>();
        String username = principal.getName();
        UserResponse user = userService.findUserByUsername(username);

        VocabularyProgressRequest request = VocabularyProgressRequest.builder()
                .userId(user.getId())
                .vocabularyId(vocabularyId)
                .practicedPronunciation(true)
                .build();

        UserVocabularyProgress updated = learningProgressService.updateVocabularyProgress(request);
        response.setResult(updated);
        return response;
    }

    @PutMapping("/secure/video/{videoId}/update-progress")
    public ApiResponse<UserVideoProgress> updateVideoProgressByPath(
            Principal principal,
            @PathVariable Integer videoId,
            @RequestBody VideoProgressRequest request) {
        ApiResponse<UserVideoProgress> response = new ApiResponse<>();
        String username = principal.getName();
        UserResponse user = userService.findUserByUsername(username);

        VideoProgressRequest progressRequest = VideoProgressRequest.builder()
                .userId(user.getId())
                .videoId(videoId)
                .watchedDuration(request.getWatchedDuration())
                .videoDuration(request.getVideoDuration())
                .lastPosition(request.getLastPosition())
                .build();

        UserVideoProgress progress = learningProgressService.updateVideoProgress(progressRequest);
        response.setResult(progress);
        return response;
    }
    @PutMapping("/secure/quiz/{quizId}/quiz-progress")
    public ApiResponse<UserQuizProgress> updateQuizProgress(Principal principal, @PathVariable Integer quizId){
        ApiResponse<UserQuizProgress> response = new ApiResponse<>();
        String username = principal.getName();
        UserResponse user = userService.findUserByUsername(username);
        UserQuizProgress progress = learningProgressService.updateProgress(user.getId(), quizId);
        response.setResult(progress);
        return response;
    }

}
