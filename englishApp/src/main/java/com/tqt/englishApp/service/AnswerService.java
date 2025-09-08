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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AnswerService {
    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private AnswerMapper answerMapper;

    private static final int PAGE_SIZE = 8;

    public AnswerResponse createAnswer(AnswerRequest answerRequest) {
        Answer answer = answerMapper.toAnswer(answerRequest);
        Quiz quiz = quizRepository.findById(answerRequest.getQuiz()).orElse(null);
        answer.setQuiz(quiz);
        return answerMapper.toAnswerResponse(answerRepository.save(answer));
    }

    public AnswerResponse updateAnswer(AnswerRequest answerRequest, Integer answerId) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new AppException(ErrorCode.ANSWER_NOT_EXISTED));
        answerMapper.updateAnswer(answer, answerRequest);
        Quiz quiz = quizRepository.findById(answerRequest.getQuiz()).orElse(null);
        answer.setQuiz(quiz);
        return  answerMapper.toAnswerResponse(answerRepository.save(answer));
    }

    public Page<AnswerResponse> getAnswer(Map<String, String> params){
        String answer = params.get("answer");
        int page = Integer.parseInt(params.getOrDefault("page", "1")) - 1;
        int size = Integer.parseInt(params.getOrDefault("size", String.valueOf(PAGE_SIZE)));

        page = Math.max(0, page);

        Pageable pageable = PageRequest.of(page, size);
        Page<Answer> result;

        if (answer == null || answer.trim().isEmpty()) {
            result = answerRepository.findAll(pageable);
        } else {
            result = answerRepository.findByAnswerContainingIgnoreCase(answer.trim(), pageable);
        }

        return result.map(answerMapper::toAnswerResponse);
    }

    public AnswerResponse getAnswerById(Integer id){
        return answerMapper.toAnswerResponse(answerRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.ANSWER_NOT_EXISTED)));
    }

    public void deleteAnswer(Integer id){
        answerRepository.deleteById(id);
    }
}
