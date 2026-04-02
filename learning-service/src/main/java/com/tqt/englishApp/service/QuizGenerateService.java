package com.tqt.englishApp.service;

import com.tqt.englishApp.dto.response.AnswerSimpleResponse;
import com.tqt.englishApp.dto.response.quiz.QuizGenerateResponse;
import com.tqt.englishApp.entity.*;
import com.tqt.englishApp.enums.QuizType;
import com.tqt.englishApp.repository.VocabularyMeaningRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizGenerateService {
    private final VocabularyMeaningRepository meaningRepository;

    public QuizGenerateResponse generateQuizEngToVn(Integer meaningId) {
        VocabularyMeaning target = meaningRepository.findById(meaningId)
                .orElseThrow(() -> new RuntimeException("Meaning not found"));

        List<AnswerSimpleResponse> answers = new ArrayList<>();
        answers.add(AnswerSimpleResponse.builder()
                .answer(target.getDefinition())
                .isCorrect(true)
                .build());

        meaningRepository.findRandomDistractors(meaningId, 3).forEach(vm ->
                answers.add(AnswerSimpleResponse.builder()
                        .answer(vm.getDefinition())
                        .isCorrect(false)
                        .build())
        );

        Collections.shuffle(answers);

        return QuizGenerateResponse.builder()
                .meanId(meaningId)
                .question("Chọn định nghĩa đúng cho từ: " + target.getVocabulary().getWord())
                .text(target.getVocabulary().getWord())
                .type(QuizType.MC)
                .answers(answers)
                .build();
    }

    public QuizGenerateResponse generateQuizVNToEng(Integer meaningId) {
        VocabularyMeaning target = meaningRepository.findById(meaningId)
                .orElseThrow(() -> new RuntimeException("Meaning not found"));

        List<AnswerSimpleResponse> answers = new ArrayList<>();
        answers.add(AnswerSimpleResponse.builder()
                .answer(target.getVocabulary().getWord())
                .isCorrect(true)
                .build());

        meaningRepository.findRandomDistractors(meaningId, 3).forEach(vm ->
                answers.add(AnswerSimpleResponse.builder()
                        .answer(vm.getVocabulary().getWord())
                        .isCorrect(false)
                        .build())
        );

        Collections.shuffle(answers);

        return QuizGenerateResponse.builder()
                .meanId(meaningId)
                .question("Chọn từ đúng cho định nghĩa: " + target.getDefinition())
                .text(target.getDefinition())
                .type(QuizType.MC)
                .answers(answers)
                .build();
    }

    public List<SessionQuiz> generateSessionQuizzes(Session session, int dailyTarget) {
        List<VocabularyMeaning> meanings = session.getMeanings();
        List<SessionQuiz> sessionQuizzes = new ArrayList<>();
        
        int mcCount, fillCount, matchCount;
        if (dailyTarget <= 5) {
            mcCount = 5; fillCount = 1; matchCount = 0;
        } else if (dailyTarget <= 15) {
            mcCount = 5; fillCount = 3; matchCount = 1;
        } else {
            mcCount = 8; fillCount = 4; matchCount = 2;
        }

        int currentIdx = 0;

        // Generate MC
        for (int i = 0; i < mcCount && currentIdx < meanings.size(); i++) {
            sessionQuizzes.add(createMC(meanings.get(currentIdx++), session));
        }

        // Generate FILL
        for (int i = 0; i < fillCount && currentIdx < meanings.size(); i++) {
            sessionQuizzes.add(createFILL(meanings.get(currentIdx++), session));
        }

        // Generate MATCH
        for (int i = 0; i < matchCount && (currentIdx + 3) < meanings.size(); i++) {
            List<VocabularyMeaning> batch = meanings.subList(currentIdx, currentIdx + 4);
            sessionQuizzes.addAll(createMATCH(batch, session, i));
            currentIdx += 4;
        }

        return sessionQuizzes;
    }

    private SessionQuiz createMC(VocabularyMeaning target, Session session) {
        Quiz quiz = Quiz.builder()
                .type(QuizType.MC)
                .question("Chọn định nghĩa đúng cho từ: " + target.getVocabulary().getWord())
                .text(target.getVocabulary().getWord())
                .build();
        
        List<Answer> answers = new ArrayList<>();
        answers.add(Answer.builder().answer(target.getDefinition()).isCorrect(true).quiz(quiz).build());

        // Distractors
        meaningRepository.findRandomDistractors(target.getId(), 3).forEach(vm -> 
            answers.add(Answer.builder().answer(vm.getDefinition()).isCorrect(false).quiz(quiz).build())
        );

        Collections.shuffle(answers);
        quiz.setAnswers(answers);

        return SessionQuiz.builder()
                .session(session)
                .quiz(quiz)
                .meaning(target)
                .xpAwarded(3)
                .build();
    }

    private SessionQuiz createFILL(VocabularyMeaning target, Session session) {
        Quiz quiz = Quiz.builder()
                .type(QuizType.FILL)
                .question("Điền từ đúng cho định nghĩa: " + target.getDefinition())
                .text(target.getDefinition())
                .build();
        
        List<Answer> answers = new ArrayList<>();
        answers.add(Answer.builder().answer(target.getVocabulary().getWord()).isCorrect(true).quiz(quiz).build());

        meaningRepository.findRandomDistractors(target.getId(), 5).forEach(vm -> 
            answers.add(Answer.builder().answer(vm.getVocabulary().getWord()).isCorrect(false).quiz(quiz).build())
        );

        Collections.shuffle(answers);
        quiz.setAnswers(answers);

        return SessionQuiz.builder()
                .session(session)
                .quiz(quiz)
                .meaning(target)
                .xpAwarded(4)
                .build();
    }

    private List<SessionQuiz> createMATCH(List<VocabularyMeaning> batch, Session session, int matchId) {
        Quiz quiz = Quiz.builder()
                .type(QuizType.MATCH)
                .question("Ghép các từ sau với nghĩa đúng")
                .text(String.valueOf(matchId))
                .build();

        List<Answer> pairs = batch.stream().map(vm -> 
            Answer.builder().answer(vm.getDefinition()).isCorrect(true).quiz(quiz).text(vm.getVocabulary().getWord()).meaningId(vm.getId()).build()
        ).collect(Collectors.toList());
        
        quiz.setAnswers(pairs);

        return batch.stream().map(vm -> 
             SessionQuiz.builder()
                    .session(session)
                    .quiz(quiz)
                    .meaning(vm)
                    .xpAwarded(5) // Reduced per pair to total 20 per round
                    .build()
        ).collect(Collectors.toList());
    }
}
