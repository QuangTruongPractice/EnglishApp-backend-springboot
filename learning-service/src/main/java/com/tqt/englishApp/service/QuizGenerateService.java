package com.tqt.englishApp.service;

import com.tqt.englishApp.dto.response.AnswerSimpleResponse;
import com.tqt.englishApp.dto.response.quiz.QuizGenerateResponse;
import com.tqt.englishApp.entity.*;
import com.tqt.englishApp.enums.QuizType;
import com.tqt.englishApp.enums.MatchSide;
import com.tqt.englishApp.enums.WritingPromptType;
import com.tqt.englishApp.repository.VocabularyMeaningRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizGenerateService {
    private final VocabularyMeaningRepository meaningRepository;

    public List<WritingPrompt> generateWritingPrompts(Session session, int dailyTarget) {
        List<VocabularyMeaning> meanings = session.getMeanings();
        List<WritingPrompt> prompts = new ArrayList<>();

        int promptCount = dailyTarget >= 30 ? 2 : 1;

        // Group meanings by SubTopic IDs
        Map<Integer, List<VocabularyMeaning>> subTopicGroups = new HashMap<>();
        for (VocabularyMeaning vm : meanings) {
            vm.getVocabulary().getSubTopics().forEach(st -> {
                subTopicGroups.computeIfAbsent(st.getId(), k -> new ArrayList<>()).add(vm);
            });
        }

        // Available subtopics with at least 2 words
        List<Integer> viableSubTopicIds = subTopicGroups.entrySet().stream()
                .filter(e -> e.getValue().size() >= 2)
                .map(Map.Entry::getKey)
                .collect(Collectors.toCollection(ArrayList::new));

        Collections.shuffle(viableSubTopicIds);

        for (int i = 0; i < promptCount; i++) {
            List<VocabularyMeaning> selected = new ArrayList<>();
            if (!viableSubTopicIds.isEmpty()) {
                Integer stId = viableSubTopicIds.remove(0);
                List<VocabularyMeaning> group = subTopicGroups.get(stId);
                Collections.shuffle(group);
                selected.add(group.get(0));
                selected.add(group.get(1));
            } else {
                // Fallback: Pick 2 random words from the pool
                List<VocabularyMeaning> pool = new ArrayList<>(meanings);
                Collections.shuffle(pool);
                if (pool.size() >= 2) {
                    selected.add(pool.get(0));
                    selected.add(pool.get(1));
                } else if (!pool.isEmpty()) {
                    selected.add(pool.get(0));
                }
            }

            String ids = selected.stream()
                    .map(m -> String.valueOf(m.getId()))
                    .collect(Collectors.joining(","));

            prompts.add(WritingPrompt.builder()
                    .session(session)
                    .type(i == 0 ? WritingPromptType.SENTENCE : WritingPromptType.MINI_STORY)
                    .targetMeaningIds(ids)
                    .completed(false)
                    .build());
        }

        return prompts;
    }

    public QuizGenerateResponse generateQuizEngToVn(Integer meaningId) {
        VocabularyMeaning target = meaningRepository.findById(meaningId)
                .orElseThrow(() -> new RuntimeException("Meaning not found"));

        LocalDateTime now = LocalDateTime.now();
        List<AnswerSimpleResponse> answers = new ArrayList<>();
        
        // Correct answer
        answers.add(AnswerSimpleResponse.builder()
                .id(1)
                .answer(target.getDefinition())
                .isCorrect(true)
                .createdAt(now)
                .build());

        // Distractors
        List<VocabularyMeaning> distractors = meaningRepository.findRandomDistractors(meaningId, 3);
        for (int i = 0; i < distractors.size(); i++) {
            answers.add(AnswerSimpleResponse.builder()
                    .id(i + 2)
                    .answer(distractors.get(i).getDefinition())
                    .isCorrect(false)
                    .createdAt(now)
                    .build());
        }

        Collections.shuffle(answers);

        return QuizGenerateResponse.builder()
                .id(-1) // Temporary ID for non-persistent quiz
                .meanId(meaningId)
                .question("Chọn định nghĩa đúng cho từ: " + target.getVocabulary().getWord())
                .text(target.getVocabulary().getWord())
                .type(QuizType.MC)
                .answers(answers)
                .createdAt(now)
                .build();
    }

    public QuizGenerateResponse generateQuizVNToEng(Integer meaningId) {
        VocabularyMeaning target = meaningRepository.findById(meaningId)
                .orElseThrow(() -> new RuntimeException("Meaning not found"));

        LocalDateTime now = LocalDateTime.now();
        List<AnswerSimpleResponse> answers = new ArrayList<>();
        
        // Correct answer
        answers.add(AnswerSimpleResponse.builder()
                .id(1)
                .answer(target.getVocabulary().getWord())
                .isCorrect(true)
                .createdAt(now)
                .build());

        // Distractors
        List<VocabularyMeaning> distractors = meaningRepository.findRandomDistractors(meaningId, 3);
        for (int i = 0; i < distractors.size(); i++) {
            answers.add(AnswerSimpleResponse.builder()
                    .id(i + 2)
                    .answer(distractors.get(i).getVocabulary().getWord())
                    .isCorrect(false)
                    .createdAt(now)
                    .build());
        }

        Collections.shuffle(answers);

        return QuizGenerateResponse.builder()
                .id(-1) // Temporary ID for non-persistent quiz
                .meanId(meaningId)
                .question("Chọn từ đúng cho định nghĩa: " + target.getDefinition())
                .text(target.getDefinition())
                .type(QuizType.MC)
                .answers(answers)
                .createdAt(now)
                .build();
    }

    public List<SessionQuiz> generateSessionQuizzes(Session session, int dailyTarget) {
        List<VocabularyMeaning> meanings = session.getMeanings();
        List<SessionQuiz> sessionQuizzes = new ArrayList<>();

        int mcCount, fillCount, matchCount;
        if (dailyTarget <= 5) {
            mcCount = 5;
            fillCount = 1;
            matchCount = 0;
        } else if (dailyTarget <= 15) {
            mcCount = 5;
            fillCount = 3;
            matchCount = 1;
        } else {
            mcCount = 8;
            fillCount = 4;
            matchCount = 2;
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
            sessionQuizzes.add(createMATCH(batch, session, i));
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

        // Distractors (Exactly 3 to make total 4)
        meaningRepository.findRandomDistractors(target.getId(), 3).forEach(
                vm -> answers.add(Answer.builder().answer(vm.getDefinition()).isCorrect(false).quiz(quiz).build()));

        Collections.shuffle(answers);
        quiz.setAnswers(answers);

        return SessionQuiz.builder()
                .session(session)
                .quiz(quiz)
                .meaning(target)
                .xpAwarded(6)
                .build();
    }

    private SessionQuiz createFILL(VocabularyMeaning target, Session session) {
        String targetWord = target.getVocabulary().getWord();
        String example = target.getExample();
        String question = "Điền từ đúng cho định nghĩa: " + target.getDefinition();
        String text = target.getDefinition();
        String correctAnswer = targetWord;

        if (example != null && !example.isEmpty()) {
            // Regex to find the target word and potential suffixes (s, ed, ing, etc.)
            String regex = "(?i)\\b(" + Pattern.quote(targetWord) + "\\w*)\\b";
            Matcher matcher = Pattern.compile(regex).matcher(example);

            if (matcher.find()) {
                String foundWord = matcher.group(1);
                correctAnswer = foundWord;
                text = example.replace(foundWord, "...");
                question = "Điền từ còn thiếu vào câu dưới đây:";
            }
        }

        Quiz quiz = Quiz.builder()
                .type(QuizType.FILL)
                .question(question)
                .text(text)
                .build();

        List<Answer> answers = new ArrayList<>();
        answers.add(Answer.builder().answer(correctAnswer).isCorrect(true).quiz(quiz).build());

        // Word bank (Exactly 5 distractors to make total 6)
        meaningRepository.findRandomDistractors(target.getId(), 5).forEach(vm -> answers
                .add(Answer.builder().answer(vm.getVocabulary().getWord()).isCorrect(false).quiz(quiz).build()));

        Collections.shuffle(answers);
        quiz.setAnswers(answers);

        return SessionQuiz.builder()
                .session(session)
                .quiz(quiz)
                .meaning(target)
                .xpAwarded(8)
                .build();
    }

    private SessionQuiz createMATCH(List<VocabularyMeaning> batch, Session session, int matchId) {
        Quiz quiz = Quiz.builder()
                .type(QuizType.MATCH)
                .question("Ghép các từ sau với nghĩa đúng")
                .text(String.valueOf(matchId))
                .build();

        List<MatchItem> items = new ArrayList<>();
        for (int i = 0; i < batch.size(); i++) {
            VocabularyMeaning vm = batch.get(i);
            String pairKey = String.valueOf(vm.getId());

            // LEFT item (Word)
            items.add(MatchItem.builder()
                    .quiz(quiz)
                    .side(MatchSide.LEFT)
                    .pairKey(pairKey)
                    .content(vm.getVocabulary().getWord())
                    .orderIndex(i)
                    .build());

            // RIGHT item (Definition)
            items.add(MatchItem.builder()
                    .quiz(quiz)
                    .side(MatchSide.RIGHT)
                    .pairKey(pairKey)
                    .content(vm.getDefinition())
                    .orderIndex(i)
                    .build());
        }

        quiz.setMatchItems(items);

        return SessionQuiz.builder()
                .session(session)
                .quiz(quiz)
                .meaning(batch.get(0)) 
                .xpAwarded(16) 
                .build();
    }
}
