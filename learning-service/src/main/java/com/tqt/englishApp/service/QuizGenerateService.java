package com.tqt.englishApp.service;

import com.tqt.englishApp.dto.response.AnswerSimpleResponse;
import com.tqt.englishApp.dto.response.quiz.QuizGenerateResponse;
import com.tqt.englishApp.entity.Vocabulary;
import com.tqt.englishApp.entity.WordMeaning;
import com.tqt.englishApp.enums.QuizType;
import com.tqt.englishApp.exception.AppException;
import com.tqt.englishApp.exception.ErrorCode;
import com.tqt.englishApp.repository.VocabularyRepository;
import com.tqt.englishApp.repository.WordMeaningRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuizGenerateService {
        @Autowired
        WordMeaningRepository wordMeaningRepository;

        @Autowired
        VocabularyRepository vocabularyRepository;

        public QuizGenerateResponse generateQuizEngToVn(Integer meanId) {
                WordMeaning correctMeaning = wordMeaningRepository.findById(meanId)
                                .orElseThrow(() -> new AppException(ErrorCode.WORD_MEANING_NOT_EXISTED));

                String word = correctMeaning.getVocabulary().getWord();
                String example = correctMeaning.getExample();
                String question;

                if (example != null && !example.isEmpty()) {
                        question = String.format("%s, “%s” có nghĩa là gì?", example, word);
                } else {
                        question = String.format("“%s” có nghĩa là gì?", word);
                }

                AnswerSimpleResponse correctAnswer = AnswerSimpleResponse.builder()
                                .answer(correctMeaning.getVnWord())
                                .isCorrect(true)
                                .build();

                List<WordMeaning> distractors = wordMeaningRepository.findRandomDistractors(meanId, 3);
                List<AnswerSimpleResponse> distractorAnswers = distractors.stream()
                                .map(dm -> AnswerSimpleResponse.builder()
                                                .answer(dm.getVnWord())
                                                .isCorrect(false)
                                                .build())
                                .collect(Collectors.toList());

                // Combine and shuffle
                List<AnswerSimpleResponse> allAnswers = new ArrayList<>();
                allAnswers.add(correctAnswer);
                allAnswers.addAll(distractorAnswers);
                Collections.shuffle(allAnswers);

                // Assign IDs
                for (int i = 0; i < allAnswers.size(); i++) {
                        allAnswers.get(i).setId(i + 1);
                }

                return QuizGenerateResponse.builder()
                                .id(1)
                                .meanId(meanId)
                                .question(question)
                                .text(word)
                                .type(QuizType.ENG_TO_VN)
                                .answers(allAnswers)
                                .createdAt(LocalDateTime.now())
                                .build();
        }

        public QuizGenerateResponse generateQuizVNToEng(Integer meanId) {
                WordMeaning correctMeaning = wordMeaningRepository.findById(meanId)
                                .orElseThrow(() -> new AppException(ErrorCode.WORD_MEANING_NOT_EXISTED));
                String word = correctMeaning.getVnWord();
                String question = String.format("Từ nào có nghĩa là “%s”?", word);

                AnswerSimpleResponse correctAnswer = AnswerSimpleResponse.builder()
                                .answer(correctMeaning.getVocabulary().getWord())
                                .isCorrect(true)
                                .build();

                Integer vocabId = correctMeaning.getVocabulary().getId();
                List<Vocabulary> distractors = vocabularyRepository.findRandomDistractors(vocabId, 3);
                List<AnswerSimpleResponse> distractorAnswers = distractors.stream()
                                .map(dm -> AnswerSimpleResponse.builder()
                                                .answer(dm.getWord())
                                                .isCorrect(false)
                                                .build())
                                .collect(Collectors.toList());

                List<AnswerSimpleResponse> allAnswers = new ArrayList<>();
                allAnswers.add(correctAnswer);
                allAnswers.addAll(distractorAnswers);
                Collections.shuffle(allAnswers);

                // Assign IDs
                for (int i = 0; i < allAnswers.size(); i++) {
                        allAnswers.get(i).setId(i + 1);
                }

                return QuizGenerateResponse.builder()
                                .id(1)
                                .meanId(meanId)
                                .question(question)
                                .text(word)
                                .type(QuizType.VN_TO_ENG)
                                .answers(allAnswers)
                                .createdAt(LocalDateTime.now())
                                .build();
        }
}
