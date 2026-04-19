package com.tqt.englishApp.service;

import com.tqt.englishApp.dto.request.QuizRequest;
import com.tqt.englishApp.dto.response.quiz.BaseQuizResponse;
import com.tqt.englishApp.dto.response.quiz.QuizDetailResponse;
import com.tqt.englishApp.entity.Quiz;
import com.tqt.englishApp.enums.QuizType;
import com.tqt.englishApp.exception.AppException;
import com.tqt.englishApp.exception.ErrorCode;
import com.tqt.englishApp.mapper.QuizMapper;
import com.tqt.englishApp.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QuizService {
    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizMapper quizMapper;

    private static final int PAGE_SIZE = 8;

    @Transactional
    public List<QuizDetailResponse> getRecentQuizzes() {
        return quizRepository.findTop5ByOrderByIdDesc().stream()
                .map(quizMapper::toQuizDetailResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public Map<String, Long> getTypeDistribution() {
        Map<String, Long> distribution = new LinkedHashMap<>();
        for (QuizType type : QuizType.values()) {
            distribution.put(type.name(), quizRepository.countByType(type));
        }
        return distribution;
    }

    @Transactional
    public List<BaseQuizResponse> getAllQuizzes() {
        return quizRepository.findAll().stream()
                .map(quizMapper::toQuizResponse)
                .collect(Collectors.toList());
    }

    public BaseQuizResponse createQuiz(QuizRequest quizRequest) {
        Quiz quiz = quizMapper.toQuiz(quizRequest);
        return quizMapper.toQuizResponse(quizRepository.save(quiz));
    }

    public BaseQuizResponse updateQuiz(QuizRequest quizRequest, Integer quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new AppException(ErrorCode.QUIZ_NOT_EXISTED));
        quizMapper.updateQuiz(quiz, quizRequest);
        return quizMapper.toQuizResponse(quizRepository.save(quiz));
    }

    @Transactional
    public Page<BaseQuizResponse> getQuiz(Map<String, String> params) {
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

    @Transactional
    public Page<QuizDetailResponse> getQuizzesAdmin(Map<String, String> params) {
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

        return result.map(quizMapper::toQuizDetailResponse);
    }

    @Transactional
    public QuizDetailResponse getQuizById(Integer id) {
        return quizMapper.toQuizDetailResponse(
                quizRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.QUIZ_NOT_EXISTED)));
    }

    public void deleteQuiz(Integer id) {
        quizRepository.deleteById(id);
    }

    public Long countQuiz() {
        return quizRepository.count();
    }
}
