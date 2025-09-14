package com.tqt.englishApp.service;

import com.tqt.englishApp.dto.request.QuizRequest;
import com.tqt.englishApp.dto.response.QuizResponse;
import com.tqt.englishApp.dto.response.SubTopicResponse;
import com.tqt.englishApp.entity.MainTopic;
import com.tqt.englishApp.entity.Quiz;
import com.tqt.englishApp.entity.SubTopic;
import com.tqt.englishApp.exception.AppException;
import com.tqt.englishApp.exception.ErrorCode;
import com.tqt.englishApp.mapper.QuizMapper;
import com.tqt.englishApp.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class QuizService {
    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizMapper quizMapper;

    private static final int PAGE_SIZE = 8;

    public QuizResponse createQuiz(QuizRequest quizRequest) {
        Quiz quiz = quizMapper.toQuiz(quizRequest);
        return quizMapper.toQuizResponse(quizRepository.save(quiz));
    }

    public QuizResponse updateQuiz(QuizRequest quizRequest, Integer quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new AppException(ErrorCode.QUIZ_NOT_EXISTED));
        quizMapper.updateQuiz(quiz, quizRequest);
        return  quizMapper.toQuizResponse(quizRepository.save(quiz));
    }

    public Page<QuizResponse> getQuiz(Map<String, String> params){
        String question = params.get("question");
        int page = Integer.parseInt(params.getOrDefault("page", "1")) - 1;
        int size = Integer.parseInt(params.getOrDefault("size", String.valueOf(PAGE_SIZE)));

        page = Math.max(0, page);

        Pageable pageable = PageRequest.of(page, size);
        Page<Quiz> result;

        if (question == null || question.trim().isEmpty()) {
            result = quizRepository.findAll(pageable);
        } else {
            result = quizRepository.findByQuestionContainingIgnoreCase(question.trim(), pageable);
        }

        return result.map(quizMapper::toQuizResponse);
    }

    public QuizResponse getQuizById(Integer id){
        return quizMapper.toQuizResponse(quizRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.QUIZ_NOT_EXISTED)));
    }

    public void deleteQuiz(Integer id){
        quizRepository.deleteById(id);
    }

    public Long countQuiz(){
        return  quizRepository.count();
    }
}
