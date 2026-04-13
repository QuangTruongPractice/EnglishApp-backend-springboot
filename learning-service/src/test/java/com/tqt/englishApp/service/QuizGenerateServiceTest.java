package com.tqt.englishApp.service;

import com.tqt.englishApp.entity.*;
import com.tqt.englishApp.enums.QuizType;
import com.tqt.englishApp.enums.WritingPromptType;
import com.tqt.englishApp.repository.VocabularyMeaningRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuizGenerateServiceTest {

    @InjectMocks
    private QuizGenerateService service;

    @Mock
    private VocabularyMeaningRepository meaningRepository;

    private VocabularyMeaning buildMeaning(int id, String word, String vnWord,
                                           String definition, String vnDefinition, String example) {
        Vocabulary vocab = new Vocabulary();
        vocab.setId(id);
        vocab.setWord(word);
        vocab.setSubTopics(new ArrayList<>());

        VocabularyMeaning meaning = new VocabularyMeaning();
        meaning.setId(id);
        meaning.setVocabulary(vocab);
        meaning.setVnWord(vnWord);
        meaning.setDefinition(definition);
        meaning.setVnDefinition(vnDefinition);
        meaning.setExample(example);
        return meaning;
    }

    private VocabularyMeaning buildSimpleMeaning(int id, String word) {
        return buildMeaning(id, word, "từ_" + id, "def_" + id, "vn_def_" + id, null);
    }

    // -----------------------------------------------------------------------
    // generateQuizEngToVn
    // -----------------------------------------------------------------------
    @Nested
    class GenerateQuizEngToVn {

        @Test
        void success_ReturnsQuizWithCorrectAnswer() {
            VocabularyMeaning target = buildMeaning(1, "happy", "hạnh phúc", "feeling joy", "cảm giác vui", null);
            when(meaningRepository.findById(1)).thenReturn(java.util.Optional.of(target));
            when(meaningRepository.findRandomDistractors(eq(1), eq(3))).thenReturn(List.of(
                    buildSimpleMeaning(2, "sad"),
                    buildSimpleMeaning(3, "angry"),
                    buildSimpleMeaning(4, "tired")
            ));

            var result = service.generateQuizEngToVn(1);

            assertNotNull(result);
            assertEquals(QuizType.MC, result.getType());
            assertTrue(result.getQuestion().contains("happy"));
            assertEquals(1, result.getMeanId());
            // Có đúng một answer đúng (vnWord của target)
            long correctCount = result.getAnswers().stream().filter(a -> Boolean.TRUE.equals(a.getIsCorrect())).count();
            assertEquals(1, correctCount);
            assertTrue(result.getAnswers().stream().anyMatch(a -> "hạnh phúc".equals(a.getAnswer()) && Boolean.TRUE.equals(a.getIsCorrect())));
        }

        @Test
        void notFound_ThrowsException() {
            when(meaningRepository.findById(999)).thenReturn(java.util.Optional.empty());

            assertThrows(RuntimeException.class, () -> service.generateQuizEngToVn(999));
        }

        @Test
        void answersAreShuffled_Contains4Answers() {
            VocabularyMeaning target = buildSimpleMeaning(1, "run");
            when(meaningRepository.findById(1)).thenReturn(java.util.Optional.of(target));
            when(meaningRepository.findRandomDistractors(eq(1), eq(3))).thenReturn(List.of(
                    buildSimpleMeaning(2, "walk"),
                    buildSimpleMeaning(3, "jump"),
                    buildSimpleMeaning(4, "swim")
            ));

            var result = service.generateQuizEngToVn(1);

            assertEquals(4, result.getAnswers().size());
        }
    }

    // -----------------------------------------------------------------------
    // generateQuizVNToEng
    // -----------------------------------------------------------------------
    @Nested
    class GenerateQuizVNToEng {

        @Test
        void success_CorrectAnswerIsTargetWord() {
            VocabularyMeaning target = buildMeaning(1, "happy", "hạnh phúc", "feeling joy", "cảm giác vui", null);
            when(meaningRepository.findById(1)).thenReturn(java.util.Optional.of(target));
            when(meaningRepository.findRandomDistractors(eq(1), eq(3))).thenReturn(List.of(
                    buildSimpleMeaning(2, "sad"),
                    buildSimpleMeaning(3, "angry"),
                    buildSimpleMeaning(4, "tired")
            ));

            var result = service.generateQuizVNToEng(1);

            assertNotNull(result);
            assertEquals(QuizType.MC, result.getType());
            assertTrue(result.getQuestion().contains("feeling joy")); // definition
            assertTrue(result.getAnswers().stream()
                    .anyMatch(a -> "happy".equals(a.getAnswer()) && Boolean.TRUE.equals(a.getIsCorrect())));
        }

        @Test
        void notFound_ThrowsException() {
            when(meaningRepository.findById(999)).thenReturn(java.util.Optional.empty());

            assertThrows(RuntimeException.class, () -> service.generateQuizVNToEng(999));
        }
    }

    // -----------------------------------------------------------------------
    // generateSessionQuizzes
    // -----------------------------------------------------------------------
    @Nested
    class GenerateSessionQuizzes {

        private Session session;
        private List<VocabularyMeaning> meanings;

        @BeforeEach
        void setUpSession() {
            session = new Session();
            meanings = new ArrayList<>();
            for (int i = 1; i <= 20; i++) {
                VocabularyMeaning m = buildMeaning(i, "word" + i, "từ" + i, "def" + i, "vn_def" + i, null);
                meanings.add(m);
            }
            session.setMeanings(meanings);
            // Distractor mock toàn cục
            when(meaningRepository.findRandomDistractors(anyInt(), anyInt())).thenReturn(List.of(
                    buildSimpleMeaning(100, "a"),
                    buildSimpleMeaning(101, "b"),
                    buildSimpleMeaning(102, "c"),
                    buildSimpleMeaning(103, "d"),
                    buildSimpleMeaning(104, "e")
            ));
        }

        @Test
        void dailyTarget5_GeneratesMCAndFill_NoMatch() {
            List<SessionQuiz> quizzes = service.generateSessionQuizzes(session, 5);

            long mcCount = quizzes.stream().filter(q -> q.getQuiz().getType() == QuizType.MC).count();
            long fillCount = quizzes.stream().filter(q -> q.getQuiz().getType() == QuizType.FILL).count();
            long matchCount = quizzes.stream().filter(q -> q.getQuiz().getType() == QuizType.MATCH).count();

            assertEquals(5, mcCount);
            assertEquals(1, fillCount);
            assertEquals(0, matchCount);
        }

        @Test
        void dailyTarget15_GeneratesMCFillAndOneMatch() {
            List<SessionQuiz> quizzes = service.generateSessionQuizzes(session, 15);

            long mcCount = quizzes.stream().filter(q -> q.getQuiz().getType() == QuizType.MC).count();
            long fillCount = quizzes.stream().filter(q -> q.getQuiz().getType() == QuizType.FILL).count();
            long matchCount = quizzes.stream().filter(q -> q.getQuiz().getType() == QuizType.MATCH).count();

            assertEquals(5, mcCount);
            assertEquals(3, fillCount);
            assertEquals(1, matchCount);
        }

        @Test
        void dailyTarget30_GeneratesMaxQuizzes() {
            List<SessionQuiz> quizzes = service.generateSessionQuizzes(session, 30);

            long mcCount = quizzes.stream().filter(q -> q.getQuiz().getType() == QuizType.MC).count();
            long fillCount = quizzes.stream().filter(q -> q.getQuiz().getType() == QuizType.FILL).count();
            long matchCount = quizzes.stream().filter(q -> q.getQuiz().getType() == QuizType.MATCH).count();

            assertEquals(8, mcCount);
            assertEquals(4, fillCount);
            assertEquals(2, matchCount);
        }

        @Test
        void mcQuiz_XpAwardedIs6() {
            List<SessionQuiz> quizzes = service.generateSessionQuizzes(session, 5);

            quizzes.stream()
                    .filter(q -> q.getQuiz().getType() == QuizType.MC)
                    .forEach(q -> assertEquals(6, q.getXpAwarded()));
        }

        @Test
        void fillQuiz_XpAwardedIs8() {
            List<SessionQuiz> quizzes = service.generateSessionQuizzes(session, 15);

            quizzes.stream()
                    .filter(q -> q.getQuiz().getType() == QuizType.FILL)
                    .forEach(q -> assertEquals(8, q.getXpAwarded()));
        }

        @Test
        void matchQuiz_XpAwardedIs16() {
            List<SessionQuiz> quizzes = service.generateSessionQuizzes(session, 30);

            quizzes.stream()
                    .filter(q -> q.getQuiz().getType() == QuizType.MATCH)
                    .forEach(q -> assertEquals(16, q.getXpAwarded()));
        }

        @Test
        void matchQuiz_Contains8MatchItems() {
            List<SessionQuiz> quizzes = service.generateSessionQuizzes(session, 30);

            quizzes.stream()
                    .filter(q -> q.getQuiz().getType() == QuizType.MATCH)
                    .forEach(q -> assertEquals(8, q.getQuiz().getMatchItems().size(),
                            "Each MATCH quiz batch of 4 words → 4 LEFT + 4 RIGHT = 8 items"));
        }

        @Test
        void fewerMeaningsThanRequired_GeneratesLessQuizzes() {
            session.setMeanings(List.of(buildMeaning(1, "word1", "từ1", "def1", "vndef1", null)));

            List<SessionQuiz> quizzes = service.generateSessionQuizzes(session, 15);

            // Chỉ có 1 meaning → chỉ tạo được 1 MC
            assertTrue(quizzes.size() <= 2);
        }
    }

    // -----------------------------------------------------------------------
    // generateWritingPrompts
    // -----------------------------------------------------------------------
    @Nested
    class GenerateWritingPrompts {

        private Session session;

        @BeforeEach
        void setUpSession() {
            session = new Session();
            List<VocabularyMeaning> meanings = new ArrayList<>();

            SubTopic st1 = new SubTopic();
            st1.setId(1);
            SubTopic st2 = new SubTopic();
            st2.setId(2);

            for (int i = 1; i <= 6; i++) {
                Vocabulary vocab = new Vocabulary();
                vocab.setId(i);
                // Alternating sub-topics để tạo viableSubTopicIds
                vocab.setSubTopics(List.of(i % 2 == 0 ? st1 : st2));

                VocabularyMeaning m = new VocabularyMeaning();
                m.setId(i);
                m.setVocabulary(vocab);
                meanings.add(m);
            }
            session.setMeanings(meanings);
        }

        @Test
        void dailyTargetBelow30_Generates1Prompt() {
            List<WritingPrompt> prompts = service.generateWritingPrompts(session, 15);

            assertEquals(1, prompts.size());
        }

        @Test
        void dailyTarget30_Generates2Prompts() {
            List<WritingPrompt> prompts = service.generateWritingPrompts(session, 30);

            assertEquals(2, prompts.size());
        }

        @Test
        void firstPromptType_IsSentence() {
            List<WritingPrompt> prompts = service.generateWritingPrompts(session, 30);

            assertEquals(WritingPromptType.SENTENCE, prompts.get(0).getType());
        }

        @Test
        void secondPromptType_IsMiniStory() {
            List<WritingPrompt> prompts = service.generateWritingPrompts(session, 30);

            assertEquals(WritingPromptType.MINI_STORY, prompts.get(1).getType());
        }

        @Test
        void promptsStartAsNotCompleted() {
            List<WritingPrompt> prompts = service.generateWritingPrompts(session, 15);

            assertFalse(prompts.get(0).getCompleted());
        }

        @Test
        void promptTargetMeaningIds_ContainsTwoIds() {
            List<WritingPrompt> prompts = service.generateWritingPrompts(session, 15);

            String ids = prompts.get(0).getTargetMeaningIds();
            assertNotNull(ids);
            assertEquals(2, ids.split(",").length);
        }
    }

    // -----------------------------------------------------------------------
    // createFILL – với example chứa từ mục tiêu
    // -----------------------------------------------------------------------
    @Test
    void fillQuiz_ExampleContainsWord_BlankIsInserted() {
        VocabularyMeaning target = buildMeaning(1, "run", "chạy",
                "move at speed", "di chuyển nhanh",
                "She can run very fast.");

        Session session = new Session();
        session.setMeanings(List.of(target));

        when(meaningRepository.findRandomDistractors(anyInt(), anyInt())).thenReturn(List.of(
                buildSimpleMeaning(2, "walk"),
                buildSimpleMeaning(3, "jump")
        ));

        List<SessionQuiz> quizzes = service.generateSessionQuizzes(session, 5); // 5 MC + 1 FILL

        SessionQuiz fillQuiz = quizzes.stream()
                .filter(q -> q.getQuiz().getType() == QuizType.FILL)
                .findFirst().orElse(null);

        if (fillQuiz != null) {
            // Text phải chứa "..." thay thế cho "run"
            assertTrue(fillQuiz.getQuiz().getText().contains("..."),
                    "FILL quiz text should contain '...' placeholder");
        }
    }
}
