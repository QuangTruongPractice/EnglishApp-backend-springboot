package com.tqt.englishApp.service;

import com.tqt.englishApp.dto.request.AnswerRequest;
import com.tqt.englishApp.dto.response.AnswerResponse;
import com.tqt.englishApp.entity.Answer;
import com.tqt.englishApp.entity.Quiz;
import com.tqt.englishApp.exception.AppException;
import com.tqt.englishApp.exception.ErrorCode;
import com.tqt.englishApp.mapper.AnswerMapper;
import com.tqt.englishApp.repository.AnswerRepository;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnswerServiceTest {

    @InjectMocks
    private AnswerService answerService;

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private AnswerMapper answerMapper;

    private Answer answer;
    private AnswerRequest answerRequest;
    private AnswerResponse answerResponse;
    private Quiz quiz;

    @BeforeEach
    void init() {
        quiz = Quiz.builder()
                .id(1)
                .question("What is Java?")
                .build();

        answer = Answer.builder()
                .id(1)
                .answer("A programming language")
                .isCorrect(true)
                .quiz(quiz)
                .build();

        answerRequest = AnswerRequest.builder()
                .answer("A programming language")
                .isCorrect(true)
                .quiz(1)
                .build();

        answerResponse = AnswerResponse.builder()
                .id(1)
                .answer("A programming language")
                .isCorrect(true)
                .quiz(quiz)
                .build();
    }

    @Test
    void createAnswer_Success() {
        when(answerMapper.toAnswer(any(AnswerRequest.class))).thenReturn(answer);
        when(quizRepository.findById(anyInt())).thenReturn(Optional.of(quiz));
        when(answerRepository.save(any(Answer.class))).thenReturn(answer);
        when(answerMapper.toAnswerResponse(any(Answer.class))).thenReturn(answerResponse);

        AnswerResponse result = answerService.createAnswer(answerRequest);

        assertNotNull(result);
        assertEquals(answerResponse.getAnswer(), result.getAnswer());
        verify(answerRepository).save(any(Answer.class));
    }

    @Test
    void createAnswer_Success_QuizNotFound() {
        when(answerMapper.toAnswer(any(AnswerRequest.class))).thenReturn(answer);
        when(quizRepository.findById(anyInt())).thenReturn(Optional.empty());
        when(answerRepository.save(any(Answer.class))).thenReturn(answer);
        when(answerMapper.toAnswerResponse(any(Answer.class))).thenReturn(answerResponse);

        AnswerResponse result = answerService.createAnswer(answerRequest);

        assertNotNull(result);
        assertNull(answer.getQuiz());
        verify(answerRepository).save(any(Answer.class));
    }

    @Test
    void updateAnswer_Success() {
        when(answerRepository.findById(anyInt())).thenReturn(Optional.of(answer));
        when(quizRepository.findById(anyInt())).thenReturn(Optional.of(quiz));
        when(answerRepository.save(any(Answer.class))).thenReturn(answer);
        when(answerMapper.toAnswerResponse(any(Answer.class))).thenReturn(answerResponse);

        AnswerResponse result = answerService.updateAnswer(answerRequest, 1);

        assertNotNull(result);
        verify(answerMapper).updateAnswer(eq(answer), eq(answerRequest));
        verify(answerRepository).save(answer);
    }

    @Test
    void updateAnswer_Fail_AnswerNotFound() {
        when(answerRepository.findById(anyInt())).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> answerService.updateAnswer(answerRequest, 1));

        assertEquals(ErrorCode.ANSWER_NOT_EXISTED, exception.getErrorCode());
        verify(answerRepository, never()).save(any());
    }

    @Test
    void updateAnswer_Success_QuizNotFound() {
        when(answerRepository.findById(anyInt())).thenReturn(Optional.of(answer));
        when(quizRepository.findById(anyInt())).thenReturn(Optional.empty());
        when(answerRepository.save(any(Answer.class))).thenReturn(answer);
        when(answerMapper.toAnswerResponse(any(Answer.class))).thenReturn(answerResponse);

        AnswerResponse result = answerService.updateAnswer(answerRequest, 1);

        assertNotNull(result);
        verify(answerRepository).save(answer);
    }

    @Test
    void getAnswer_WithKeyword() {
        Map<String, String> params = new HashMap<>();
        params.put("answer", "Java");
        params.put("page", "1");
        params.put("size", "5");

        Page<Answer> page = new PageImpl<>(List.of(answer));
        when(answerRepository.findByAnswerContainingIgnoreCase(eq("Java"), any(Pageable.class))).thenReturn(page);
        when(answerMapper.toAnswerResponse(any(Answer.class))).thenReturn(answerResponse);

        Page<AnswerResponse> result = answerService.getAnswer(params);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(answerRepository).findByAnswerContainingIgnoreCase(eq("Java"), any(Pageable.class));
    }

    @Test
    void getAnswer_WithoutKeyword_ReturnsAll() {
        Map<String, String> params = new HashMap<>();
        params.put("page", "1");

        Page<Answer> page = new PageImpl<>(List.of(answer));
        when(answerRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(answerMapper.toAnswerResponse(any(Answer.class))).thenReturn(answerResponse);

        Page<AnswerResponse> result = answerService.getAnswer(params);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(answerRepository).findAll(any(Pageable.class));
    }

    @Test
    void getAnswer_WithBlankKeyword_ReturnsAll() {
        Map<String, String> params = new HashMap<>();
        params.put("answer", "   ");

        Page<Answer> page = new PageImpl<>(List.of(answer));
        when(answerRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(answerMapper.toAnswerResponse(any(Answer.class))).thenReturn(answerResponse);

        Page<AnswerResponse> result = answerService.getAnswer(params);

        assertNotNull(result);
        verify(answerRepository).findAll(any(Pageable.class));
    }

    @Test
    void getAnswer_NegativePage_AdjustsToZero() {
        Map<String, String> params = new HashMap<>();
        params.put("page", "-5");

        Page<Answer> page = new PageImpl<>(List.of(answer));
        org.mockito.ArgumentCaptor<Pageable> pageableCaptor = org.mockito.ArgumentCaptor.forClass(Pageable.class);
        when(answerRepository.findAll(pageableCaptor.capture())).thenReturn(page);
        when(answerMapper.toAnswerResponse(any(Answer.class))).thenReturn(answerResponse);

        answerService.getAnswer(params);

        assertEquals(0, pageableCaptor.getValue().getPageNumber());
    }

    @Test
    void getAnswerById_Success() {
        when(answerRepository.findById(anyInt())).thenReturn(Optional.of(answer));
        when(answerMapper.toAnswerResponse(any(Answer.class))).thenReturn(answerResponse);

        AnswerResponse result = answerService.getAnswerById(1);

        assertNotNull(result);
        assertEquals(answerResponse.getId(), result.getId());
    }

    @Test
    void getAnswerById_Fail_NotFound() {
        when(answerRepository.findById(anyInt())).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> answerService.getAnswerById(1));

        assertEquals(ErrorCode.ANSWER_NOT_EXISTED, exception.getErrorCode());
    }

    @Test
    void deleteAnswer_Success() {
        doNothing().when(answerRepository).deleteById(anyInt());

        answerService.deleteAnswer(1);

        verify(answerRepository).deleteById(1);
    }
}
