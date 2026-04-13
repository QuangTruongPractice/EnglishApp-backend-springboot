package com.tqt.englishApp.service;

import com.tqt.englishApp.dto.response.SessionQuizResponse;
import com.tqt.englishApp.dto.response.SessionResponse;
import com.tqt.englishApp.dto.response.SubmitQuizResponse;
import com.tqt.englishApp.entity.*;
import com.tqt.englishApp.enums.QuizType;
import com.tqt.englishApp.mapper.SessionMapper;
import com.tqt.englishApp.mapper.SessionQuizMapper;
import com.tqt.englishApp.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @InjectMocks
    private SessionService service;

    @Mock private SessionRepository sessionRepository;
    @Mock private UserLearningProfileRepository profileRepository;
    @Mock private SessionQuizRepository sessionQuizRepository;
    @Mock private WritingPromptRepository writingPromptRepository;
    @Mock private VocabularyMeaningRepository meaningRepository;
    @Mock private VocabularySelectionService selectionService;
    @Mock private QuizGenerateService quizService;
    @Mock private LevelService levelService;
    @Mock private VocabularyLearningService vocabularyLearningService;
    @Mock private SessionMapper sessionMapper;
    @Mock private SessionQuizMapper sessionQuizMapper;

    private Session session;
    private Quiz mcQuiz;
    private SessionQuiz sessionQuiz;
    private VocabularyMeaning meaning;

    @BeforeEach
    void setUp() {
        meaning = new VocabularyMeaning();
        meaning.setId(1);
        meaning.setVocabulary(new Vocabulary());

        mcQuiz = new Quiz();
        mcQuiz.setId(1);
        mcQuiz.setType(QuizType.MC);

        session = new Session();
        session.setId(10);
        session.setUserId("user-1");
        session.setDate(LocalDate.now());
        session.setTotalXP(0);
        session.setIsLevelUp(false);

        sessionQuiz = new SessionQuiz();
        sessionQuiz.setId(100);
        sessionQuiz.setSession(session);
        sessionQuiz.setQuiz(mcQuiz);
        sessionQuiz.setMeaning(meaning);
        sessionQuiz.setXpAwarded(6);
        sessionQuiz.setIsCorrect(null);
        sessionQuiz.setRetryAttempt(0);
    }

    // -----------------------------------------------------------------------
    // getOrCreateSession
    // -----------------------------------------------------------------------
    @Nested
    class GetOrCreateSession {

        @Test
        void existingSession_ReturnsIt() {
            SessionResponse expected = new SessionResponse();
            when(sessionRepository.findByUserIdAndDate(eq("user-1"), any(LocalDate.class)))
                    .thenReturn(Optional.of(session));
            when(sessionMapper.toSessionResponse(session)).thenReturn(expected);

            SessionResponse result = service.getOrCreateSession("user-1");

            assertNotNull(result);
            verify(sessionRepository, never()).save(any());
        }

        @Test
        void noExistingSession_CreatesNewOne() {
            UserLearningProfile profile = new UserLearningProfile();
            profile.setUserId("user-1");
            profile.setDailyTarget(15);

            when(sessionRepository.findByUserIdAndDate(anyString(), any())).thenReturn(Optional.empty());
            when(profileRepository.findByUserId("user-1")).thenReturn(Optional.of(profile));
            when(selectionService.selectMeaningsForSession(any())).thenReturn(new ArrayList<>());
            when(quizService.generateSessionQuizzes(any(), anyInt())).thenReturn(new ArrayList<>());
            when(quizService.generateWritingPrompts(any(), anyInt())).thenReturn(new ArrayList<>());
            when(sessionRepository.save(any())).thenReturn(session);
            when(sessionMapper.toSessionResponse(any())).thenReturn(new SessionResponse());

            SessionResponse result = service.getOrCreateSession("user-1");

            assertNotNull(result);
            verify(sessionRepository).save(any(Session.class));
            verify(selectionService).selectMeaningsForSession(profile);
        }

        @Test
        void noProfile_ThrowsRuntimeException() {
            when(sessionRepository.findByUserIdAndDate(anyString(), any())).thenReturn(Optional.empty());
            when(profileRepository.findByUserId("ghost")).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class, () -> service.getOrCreateSession("ghost"));
        }
    }

    // -----------------------------------------------------------------------
    // submitQuiz – MC (correct)
    // -----------------------------------------------------------------------
    @Nested
    class SubmitQuiz {

        @Test
        void correctAnswer_AwardsXpAndSavesSession() {
            when(sessionRepository.findById(10)).thenReturn(Optional.of(session));
            when(sessionQuizRepository.findByIdAndSessionId(100, 10)).thenReturn(Optional.of(sessionQuiz));
            when(sessionQuizRepository.save(any())).thenReturn(sessionQuiz);
            when(profileRepository.findByUserId("user-1")).thenReturn(Optional.of(new UserLearningProfile()));
            when(sessionRepository.save(any())).thenReturn(session);

            SubmitQuizResponse result = service.submitQuiz(10, 100, "user-1", true, 2000L);

            assertNotNull(result);
            assertEquals(6, result.getXpAwarded());
            assertNull(result.getRetryQuiz());
            verify(levelService).addXpAndCheckLevelUp(any(), eq(session), eq(6));
            verify(vocabularyLearningService).updateVocabularyProgress(any());
        }

        @Test
        void wrongAnswer_FirstAttempt_CreatesRetryQuiz() {
            sessionQuiz.setRetryAttempt(0);
            SessionQuiz retryQuiz = new SessionQuiz();
            retryQuiz.setId(200);
            retryQuiz.setXpAwarded(3);
            retryQuiz.setRetryAttempt(1);
            retryQuiz.setQuiz(mcQuiz);
            retryQuiz.setMeaning(meaning);

            SessionQuizResponse retryResponse = new SessionQuizResponse();

            when(sessionRepository.findById(10)).thenReturn(Optional.of(session));
            when(sessionQuizRepository.findByIdAndSessionId(100, 10)).thenReturn(Optional.of(sessionQuiz));
            when(sessionQuizRepository.save(sessionQuiz)).thenReturn(sessionQuiz);
            when(sessionQuizRepository.save(argThat(sq -> sq != sessionQuiz))).thenReturn(retryQuiz);
            when(sessionQuizMapper.toSessionQuizResponse(retryQuiz)).thenReturn(retryResponse);

            SubmitQuizResponse result = service.submitQuiz(10, 100, "user-1", false, 5000L);

            assertNotNull(result);
            assertEquals(0, result.getXpAwarded()); // không có XP khi sai
            assertNotNull(result.getRetryQuiz(), "Should create retry quiz on first wrong attempt");
            verify(vocabularyLearningService).updateVocabularyProgress(any());
        }

        @Test
        void wrongAnswer_SecondAttempt_RetryAttempt1_CreatesAnotherRetry() {
            sessionQuiz.setRetryAttempt(1);
            sessionQuiz.setXpAwarded(3);

            SessionQuiz retryQuiz = new SessionQuiz();
            retryQuiz.setId(201);
            retryQuiz.setRetryAttempt(2);
            retryQuiz.setXpAwarded(2); // ceil(3/2)
            retryQuiz.setQuiz(mcQuiz);
            retryQuiz.setMeaning(meaning);

            SessionQuizResponse retryResponse = new SessionQuizResponse();

            when(sessionRepository.findById(10)).thenReturn(Optional.of(session));
            when(sessionQuizRepository.findByIdAndSessionId(100, 10)).thenReturn(Optional.of(sessionQuiz));
            when(sessionQuizRepository.save(sessionQuiz)).thenReturn(sessionQuiz);
            when(sessionQuizRepository.save(argThat(sq -> sq != sessionQuiz))).thenReturn(retryQuiz);
            when(sessionQuizMapper.toSessionQuizResponse(retryQuiz)).thenReturn(retryResponse);

            SubmitQuizResponse result = service.submitQuiz(10, 100, "user-1", false, 5000L);

            assertNotNull(result.getRetryQuiz());
            // XP của retry = ceil(3/2) = 2
            verify(sessionQuizRepository, times(2)).save(any());
        }

        @Test
        void wrongAnswer_ThirdAttempt_NoMoreRetry() {
            sessionQuiz.setRetryAttempt(2); // đã hết lượt retry

            when(sessionRepository.findById(10)).thenReturn(Optional.of(session));
            when(sessionQuizRepository.findByIdAndSessionId(100, 10)).thenReturn(Optional.of(sessionQuiz));
            when(sessionQuizRepository.save(sessionQuiz)).thenReturn(sessionQuiz);

            SubmitQuizResponse result = service.submitQuiz(10, 100, "user-1", false, 5000L);

            assertNull(result.getRetryQuiz(), "No retry quiz after 2 retries");
            // Chỉ lưu sessionQuiz gốc, không tạo thêm
            verify(sessionQuizRepository, times(1)).save(any());
        }

        @Test
        void sessionNotFound_ThrowsException() {
            when(sessionRepository.findById(999)).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class,
                    () -> service.submitQuiz(999, 100, "user-1", true, 1000L));
        }

        @Test
        void sessionQuizNotFound_ThrowsException() {
            when(sessionRepository.findById(10)).thenReturn(Optional.of(session));
            when(sessionQuizRepository.findByIdAndSessionId(999, 10)).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class,
                    () -> service.submitQuiz(10, 999, "user-1", true, 1000L));
        }

        @Test
        void correctAnswer_ProfileNotFound_ThrowsException() {
            when(sessionRepository.findById(10)).thenReturn(Optional.of(session));
            when(sessionQuizRepository.findByIdAndSessionId(100, 10)).thenReturn(Optional.of(sessionQuiz));
            when(sessionQuizRepository.save(any())).thenReturn(sessionQuiz);
            when(profileRepository.findByUserId("user-1")).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class,
                    () -> service.submitQuiz(10, 100, "user-1", true, 2000L));
        }

        @Test
        void matchQuiz_CorrectAnswer_UpdatesAllMeanings() {
            Quiz matchQuiz = new Quiz();
            matchQuiz.setId(2);
            matchQuiz.setType(QuizType.MATCH);

            MatchItem item1 = new MatchItem();
            item1.setPairKey("1");
            MatchItem item2 = new MatchItem();
            item2.setPairKey("2");
            matchQuiz.setMatchItems(List.of(item1, item2));

            SessionQuiz matchSessionQuiz = new SessionQuiz();
            matchSessionQuiz.setId(101);
            matchSessionQuiz.setSession(session);
            matchSessionQuiz.setQuiz(matchQuiz);
            matchSessionQuiz.setMeaning(meaning);
            matchSessionQuiz.setXpAwarded(16);
            matchSessionQuiz.setIsCorrect(null);
            matchSessionQuiz.setRetryAttempt(0);

            when(sessionRepository.findById(10)).thenReturn(Optional.of(session));
            when(sessionQuizRepository.findByIdAndSessionId(101, 10)).thenReturn(Optional.of(matchSessionQuiz));
            when(sessionQuizRepository.save(any())).thenReturn(matchSessionQuiz);
            when(profileRepository.findByUserId("user-1")).thenReturn(Optional.of(new UserLearningProfile()));
            when(sessionRepository.save(any())).thenReturn(session);

            service.submitQuiz(10, 101, "user-1", true, 3000L);

            // Cập nhật vocabulary progress cho 2 meanings (item1 và item2)
            verify(vocabularyLearningService, times(2)).updateVocabularyProgress(any());
        }

        @Test
        void retryQuizXp_IsHalfOfOriginal_RoundedUp() {
            sessionQuiz.setXpAwarded(6);
            sessionQuiz.setRetryAttempt(0);

            SessionQuiz retryQuiz = new SessionQuiz();
            retryQuiz.setId(200);
            retryQuiz.setRetryAttempt(1);
            retryQuiz.setXpAwarded(3); // ceil(6/2) = 3
            retryQuiz.setQuiz(mcQuiz);
            retryQuiz.setMeaning(meaning);

            when(sessionRepository.findById(10)).thenReturn(Optional.of(session));
            when(sessionQuizRepository.findByIdAndSessionId(100, 10)).thenReturn(Optional.of(sessionQuiz));
            when(sessionQuizRepository.save(sessionQuiz)).thenReturn(sessionQuiz);
            when(sessionQuizRepository.save(argThat(sq -> sq != sessionQuiz))).thenReturn(retryQuiz);
            when(sessionQuizMapper.toSessionQuizResponse(retryQuiz)).thenReturn(new SessionQuizResponse());

            service.submitQuiz(10, 100, "user-1", false, 5000L);

            // Xác nhận XP của retry quiz = ceil(6/2) = 3
            verify(sessionQuizRepository).save(argThat(sq ->
                    sq != sessionQuiz && sq.getXpAwarded() == 3));
        }
    }

    // -----------------------------------------------------------------------
    // checkLevelUp
    // -----------------------------------------------------------------------
    @Nested
    class CheckLevelUp {

        @Test
        void levelUpTrue_ResetsFlag_ReturnsTrue() {
            session.setIsLevelUp(true);
            when(sessionRepository.findById(10)).thenReturn(Optional.of(session));
            when(sessionRepository.save(any())).thenReturn(session);

            boolean result = service.checkLevelUp(10);

            assertTrue(result);
            assertFalse(session.getIsLevelUp(), "Flag should be reset after reading");
            verify(sessionRepository).save(session);
        }

        @Test
        void levelUpFalse_DoesNotSave_ReturnsFalse() {
            session.setIsLevelUp(false);
            when(sessionRepository.findById(10)).thenReturn(Optional.of(session));

            boolean result = service.checkLevelUp(10);

            assertFalse(result);
            verify(sessionRepository, never()).save(any());
        }

        @Test
        void sessionNotFound_ThrowsException() {
            when(sessionRepository.findById(999)).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class, () -> service.checkLevelUp(999));
        }
    }
}
