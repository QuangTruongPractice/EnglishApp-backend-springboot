package com.tqt.englishApp.service;

import com.tqt.englishApp.dto.request.QuizRequest;
import com.tqt.englishApp.dto.response.QuizResponse;
import com.tqt.englishApp.entity.Quiz;
import com.tqt.englishApp.exception.AppException;
import com.tqt.englishApp.exception.ErrorCode;
import com.tqt.englishApp.mapper.QuizMapper;
import com.tqt.englishApp.repository.QuizRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuizServiceTest {

    @InjectMocks
    private QuizService quizService;

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private QuizMapper quizMapper;

    private Quiz quiz;
    private QuizRequest quizRequest;
    private QuizResponse quizResponse;

    @BeforeEach
    void init() {
        quiz = Quiz.builder().id(1).question("Test Question").build();
        quizRequest = QuizRequest.builder().question("Updated Question").build();
        quizResponse = QuizResponse.builder().id(1).question("Test Question").build();
    }

    @Test
    void createQuiz_Success() {
        when(quizMapper.toQuiz(quizRequest)).thenReturn(quiz);
        when(quizRepository.save(quiz)).thenReturn(quiz);
        when(quizMapper.toQuizResponse(quiz)).thenReturn(quizResponse);

        QuizResponse result = quizService.createQuiz(quizRequest);

        assertNotNull(result);
        verify(quizRepository).save(quiz);
    }

    @Test
    void updateQuiz_Success() {
        when(quizRepository.findById(1)).thenReturn(Optional.of(quiz));
        when(quizRepository.save(quiz)).thenReturn(quiz);
        when(quizMapper.toQuizResponse(quiz)).thenReturn(quizResponse);

        QuizResponse result = quizService.updateQuiz(quizRequest, 1);

        assertNotNull(result);
        verify(quizMapper).updateQuiz(quiz, quizRequest);
        verify(quizRepository).save(quiz);
    }

    @Test
    void updateQuiz_NotFound() {
        when(quizRepository.findById(1)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> quizService.updateQuiz(quizRequest, 1));

        assertEquals(ErrorCode.QUIZ_NOT_EXISTED, exception.getErrorCode());
    }

    @Test
    void getQuiz_NoFilter_Success() {
        Map<String, String> params = new HashMap<>();
        Page<Quiz> quizPage = new PageImpl<>(List.of(quiz));

        when(quizRepository.findAll(any(Pageable.class))).thenReturn(quizPage);
        when(quizMapper.toQuizResponse(any(Quiz.class))).thenReturn(quizResponse);

        Page<QuizResponse> result = quizService.getQuiz(params);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(quizRepository).findAll(any(Pageable.class));
    }

    @Test
    void getQuiz_WithFilter_Success() {
        Map<String, String> params = new HashMap<>();
        params.put("question", "search");
        Page<Quiz> quizPage = new PageImpl<>(List.of(quiz));

        when(quizRepository.findByQuestionContainingIgnoreCase(eq("search"), any(Pageable.class)))
                .thenReturn(quizPage);
        when(quizMapper.toQuizResponse(any(Quiz.class))).thenReturn(quizResponse);

        Page<QuizResponse> result = quizService.getQuiz(params);

        assertNotNull(result);
        verify(quizRepository).findByQuestionContainingIgnoreCase(eq("search"), any(Pageable.class));
    }

    @Test
    void getQuizById_Success() {
        when(quizRepository.findById(1)).thenReturn(Optional.of(quiz));
        when(quizMapper.toQuizResponse(quiz)).thenReturn(quizResponse);

        QuizResponse result = quizService.getQuizById(1);

        assertNotNull(result);
    }

    @Test
    void getQuizById_NotFound() {
        when(quizRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(AppException.class, () -> quizService.getQuizById(1));
    }

    @Test
    void deleteQuiz_Success() {
        quizService.deleteQuiz(1);
        verify(quizRepository).deleteById(1);
    }

    @Test
    void countQuiz_Success() {
        when(quizRepository.count()).thenReturn(10L);
        assertEquals(10L, quizService.countQuiz());
    }
}
