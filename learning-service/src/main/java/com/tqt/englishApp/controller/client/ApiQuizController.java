package com.tqt.englishApp.controller.client;

import com.tqt.englishApp.dto.request.ApiResponse;
import com.tqt.englishApp.dto.request.VocabularyProgressRequest;
import com.tqt.englishApp.dto.response.quiz.BaseQuizResponse;
import com.tqt.englishApp.dto.response.quiz.QuizDetailResponse;
import com.tqt.englishApp.dto.response.quiz.QuizGenerateResponse;
import com.tqt.englishApp.entity.UserVocabularyProgress;
import com.tqt.englishApp.service.QuizGenerateService;
import com.tqt.englishApp.service.QuizService;
import com.tqt.englishApp.service.VocabularyLearningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiQuizController {
    @Autowired
    private QuizService quizService;

    @Autowired
    private QuizGenerateService quizGenerateService;

    @Autowired
    private VocabularyLearningService vocabularyLearningService;

    private final Random random = new Random();

    @GetMapping("/quiz")
    public ApiResponse<Page<BaseQuizResponse>> getQuizzes(@RequestParam Map<String, String> params) {
        ApiResponse<Page<BaseQuizResponse>> response = new ApiResponse<>();
        response.setResult(quizService.getQuiz(params));
        return response;
    }

    @GetMapping("/quiz/{id}")
    public ApiResponse<QuizDetailResponse> getQuiz(@PathVariable("id") Integer id) {
        ApiResponse<QuizDetailResponse> response = new ApiResponse<>();
        response.setResult(quizService.getQuizById(id));
        return response;
    }

    @GetMapping("/quiz/generate")
    public ApiResponse<QuizGenerateResponse> generateQuiz(@RequestParam("meanId") Integer meanId) {
        ApiResponse<QuizGenerateResponse> response = new ApiResponse<>();

        QuizGenerateResponse quiz;
        if (random.nextBoolean()) {
            quiz = quizGenerateService.generateQuizEngToVn(meanId);
        } else {
            quiz = quizGenerateService.generateQuizVNToEng(meanId);
        }

        response.setResult(quiz);
        return response;
    }

    @PostMapping("/secure/quiz/submit")
    public ApiResponse<UserVocabularyProgress> submitQuizResult(
            Principal principal,
            @RequestBody VocabularyProgressRequest request) {
        ApiResponse<UserVocabularyProgress> response = new ApiResponse<>();

        request.setUserId(principal.getName());
        UserVocabularyProgress updated = vocabularyLearningService.updateVocabularyProgress(request);

        response.setResult(updated);
        return response;
    }
}
