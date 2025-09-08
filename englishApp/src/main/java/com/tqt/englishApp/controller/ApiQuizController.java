package com.tqt.englishApp.controller;

import com.tqt.englishApp.dto.request.ApiResponse;
import com.tqt.englishApp.dto.response.QuizResponse;
import com.tqt.englishApp.service.AnswerService;
import com.tqt.englishApp.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiQuizController {
    @Autowired
    private QuizService quizService;

    @GetMapping("/quiz")
    public ApiResponse<Page<QuizResponse>> getMainTopics(@RequestParam Map<String, String> params) {
        ApiResponse<Page<QuizResponse>> response = new ApiResponse<>();
        response.setResult(quizService.getQuiz(params));
        return response;
    }

    @GetMapping("/quiz/{id}")
    public ApiResponse<QuizResponse> getMainTopic(@PathVariable("id") Integer id) {
        ApiResponse<QuizResponse> response = new ApiResponse<>();
        response.setResult(quizService.getQuizById(id));
        return response;
    }

}
