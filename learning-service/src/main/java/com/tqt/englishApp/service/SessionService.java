package com.tqt.englishApp.service;

import com.tqt.englishApp.entity.*;
import com.tqt.englishApp.repository.SessionRepository;
import com.tqt.englishApp.repository.UserLearningProfileRepository;
import com.tqt.englishApp.repository.SessionQuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.tqt.englishApp.dto.request.VocabularyProgressRequest;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final SessionRepository sessionRepository;
    private final UserLearningProfileRepository profileRepository;
    private final SessionQuizRepository sessionQuizRepository;
    private final VocabularySelectionService selectionService;
    private final QuizGenerateService quizService;
    private final LevelService levelService;
    private final VocabularyLearningService vocabularyLearningService;

    @Transactional
    public int submitQuiz(Integer sessionId, Integer quizId, String userId, boolean isCorrect) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        
        SessionQuiz sessionQuiz = sessionQuizRepository.findBySessionIdAndQuizId(sessionId, quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found in this session"));

        sessionQuiz.setIsCorrect(isCorrect);
        sessionQuizRepository.save(sessionQuiz);

        VocabularyProgressRequest progressRequest = new VocabularyProgressRequest();
        progressRequest.setUserId(userId);
        progressRequest.setMeaningId(sessionQuiz.getMeaning().getId());
        progressRequest.setIsCorrect(isCorrect);
        vocabularyLearningService.updateVocabularyProgress(progressRequest);

        if (!isCorrect) return 0;

        int xpAwarded = sessionQuiz.getXpAwarded();
        UserLearningProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        
        levelService.addXpAndCheckLevelUp(profile, session, xpAwarded);
        
        session.setTotalXP(session.getTotalXP() + xpAwarded);
        sessionRepository.save(session);
        
        return xpAwarded;
    }

    @Transactional
    public int submitWriting(Integer sessionId, String userId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        UserLearningProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        int xpAwarded = 10; 
        levelService.addXpAndCheckLevelUp(profile, session, xpAwarded);
        
        session.setTotalXP(session.getTotalXP() + xpAwarded);
        sessionRepository.save(session);
        return xpAwarded;
    }

    @Transactional
    public boolean checkLevelUp(Integer sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        
        boolean levelUp = session.getIsLevelUp();
        if (levelUp) {
            session.setIsLevelUp(false);
            sessionRepository.save(session);
        }
        return levelUp;
    }

    @Transactional
    public Session getOrCreateDailySession(String userId) {
        return sessionRepository.findByUserIdAndDate(userId, LocalDate.now())
                .orElseGet(() -> createDailySession(userId));
    }

    private Session createDailySession(String userId) {
        UserLearningProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User learning profile not found"));

        List<VocabularyMeaning> selectedMeanings = selectionService.selectMeaningsForSession(profile);

        Session session = Session.builder()
                .userId(userId)
                .date(LocalDate.now())
                .meanings(selectedMeanings)
                .totalXP(0)
                .completed(false)
                .build();

        List<SessionQuiz> sessionQuizzes = quizService.generateSessionQuizzes(session, profile.getDailyTarget());
        session.setQuizzes(sessionQuizzes);
        
        // Save session first to get ID
        Session savedSession = sessionRepository.save(session);

        // Writing prompts
        List<VocabularyMeaning> usedInQuizzes = sessionQuizzes.stream().map(SessionQuiz::getMeaning).distinct().collect(Collectors.toList());
        List<VocabularyMeaning> remaining = selectedMeanings.stream()
                .filter(m -> !usedInQuizzes.contains(m))
                .collect(Collectors.toList());

        List<WritingPrompt> prompts = new ArrayList<>();
        int writingPromptCount = getWritingPromptCount(profile.getDailyTarget());
        for (int i = 0; i < writingPromptCount && remaining.size() >= 2; i++) {
            prompts.add(WritingPrompt.builder()
                    .session(savedSession)
                    .meanings(List.of(remaining.get(0), remaining.get(1)))
                    .build());
            remaining.remove(0);
            remaining.remove(0);
        }
        savedSession.setWritingPrompts(prompts);

        return sessionRepository.save(savedSession);
    }

    private int getWritingPromptCount(int dailyTarget) {
        if (dailyTarget <= 5) return 0;
        if (dailyTarget <= 15) return 1;
        return 2;
    }
}
