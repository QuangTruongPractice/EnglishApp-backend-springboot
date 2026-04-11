package com.tqt.englishApp.service;

import com.tqt.englishApp.entity.*;
import com.tqt.englishApp.repository.SessionRepository;
import com.tqt.englishApp.repository.UserLearningProfileRepository;
import com.tqt.englishApp.repository.SessionQuizRepository;
import com.tqt.englishApp.repository.WritingPromptRepository;
import com.tqt.englishApp.repository.VocabularyMeaningRepository;
import com.tqt.englishApp.mapper.SessionMapper; 
import com.tqt.englishApp.dto.response.AiAnalysisResponse;
import com.tqt.englishApp.dto.response.SessionResponse;
import com.tqt.englishApp.dto.request.VocabularyProgressRequest;
import com.tqt.englishApp.enums.QuizType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final SessionRepository sessionRepository;
    private final UserLearningProfileRepository profileRepository;
    private final SessionQuizRepository sessionQuizRepository;
    private final WritingPromptRepository writingPromptRepository;
    private final VocabularyMeaningRepository meaningRepository;
    private final VocabularySelectionService selectionService;
    private final QuizGenerateService quizService;
    private final LevelService levelService;
    private final VocabularyLearningService vocabularyLearningService;
    private final SessionMapper sessionMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    @Transactional
    public AiAnalysisResponse submitWriting(Integer sessionId, Integer promptId, String userId, String userText) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        WritingPrompt prompt = writingPromptRepository.findById(promptId)
                .orElseThrow(() -> new RuntimeException("Writing prompt not found"));

        if (!prompt.getSession().getId().equals(sessionId)) {
            throw new RuntimeException("Writing prompt does not belong to this session");
        }

        // Call External AI API
        String aiUrl = "https://satyr-dashing-officially.ngrok-free.app/analyze-usage";

        List<Integer> ids = Arrays.stream(prompt.getTargetMeaningIds().split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        String[] keywords = ids.stream()
                .map(id -> meaningRepository.findById(id).orElse(null))
                .filter(Objects::nonNull)
                .map(m -> m.getVocabulary().getWord())
                .toArray(String[]::new);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("text", userText);
        requestBody.put("words", keywords);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        AiAnalysisResponse aiResponse;
        try {
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            aiResponse = restTemplate.postForObject(aiUrl, entity, AiAnalysisResponse.class);
        } catch (Exception e) {
            aiResponse = AiAnalysisResponse.builder()
                    .score(0)
                    .improved_sentence("AI analysis failed: " + e.getMessage())
                    .build();
        }

        int xpAwarded = Math.max(aiResponse != null ? aiResponse.getScore() : 0, 2);

        UserLearningProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        levelService.addXpAndCheckLevelUp(profile, session, xpAwarded);
        session.setTotalXP(session.getTotalXP() + xpAwarded);

        prompt.setUserResponse(userText);
        prompt.setScore(xpAwarded);
        prompt.setImprovedSentence(aiResponse != null ? aiResponse.getImproved_sentence() : "");
        prompt.setCompleted(true);

        writingPromptRepository.save(prompt);
        sessionRepository.save(session);

        return aiResponse;
    }

    @Transactional
    public int submitQuiz(Integer sessionId, Integer sessionQuizId, String userId, boolean isCorrect) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        SessionQuiz sessionQuiz = sessionQuizRepository.findByIdAndSessionId(sessionQuizId, sessionId)
                .orElseThrow(() -> new RuntimeException("Quiz not found in this session"));

        sessionQuiz.setIsCorrect(isCorrect);
        sessionQuizRepository.save(sessionQuiz);

        if (sessionQuiz.getQuiz().getType() == QuizType.MATCH) {
            Set<Integer> meaningIds = sessionQuiz.getQuiz().getMatchItems().stream()
                    .map(item -> Integer.parseInt(item.getPairKey()))
                    .collect(Collectors.toSet());

            for (Integer mId : meaningIds) {
                VocabularyProgressRequest progressRequest = new VocabularyProgressRequest();
                progressRequest.setUserId(userId);
                progressRequest.setMeaningId(mId);
                progressRequest.setIsCorrect(isCorrect);
                vocabularyLearningService.updateVocabularyProgress(progressRequest);
            }
        } else {
            VocabularyProgressRequest progressRequest = new VocabularyProgressRequest();
            progressRequest.setUserId(userId);
            progressRequest.setMeaningId(sessionQuiz.getMeaning().getId());
            progressRequest.setIsCorrect(isCorrect);
            vocabularyLearningService.updateVocabularyProgress(progressRequest);
        }

        int xpAwarded = 0;
        if (isCorrect) {
            xpAwarded = sessionQuiz.getXpAwarded();
            UserLearningProfile profile = profileRepository.findByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("Profile not found"));

            levelService.addXpAndCheckLevelUp(profile, session, xpAwarded);
            session.setTotalXP(session.getTotalXP() + xpAwarded);
            sessionRepository.save(session);
        }

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
    public SessionResponse getOrCreateSession(String userId) {
        Session session = sessionRepository.findByUserIdAndDate(userId, LocalDate.now())
                .orElseGet(() -> createDailySession(userId));
        return sessionMapper.toSessionResponse(session);
    }

    private Session createDailySession(String userId) {
        UserLearningProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User learning profile not found"));

        levelService.syncUserLevel(profile);

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

        List<WritingPrompt> prompts = quizService.generateWritingPrompts(session, profile.getDailyTarget());
        session.setWritingPrompts(prompts);

        return sessionRepository.save(session);
    }
}
