package com.tqt.englishApp.service;

import com.tqt.englishApp.dto.request.DiagnosticQuizRequest;
import com.tqt.englishApp.dto.response.PlacementQuizResponse;
import com.tqt.englishApp.dto.response.quiz.QuizGenerateResponse;
import com.tqt.englishApp.entity.UserLearningProfile;
import com.tqt.englishApp.entity.Vocabulary;
import com.tqt.englishApp.entity.VocabularyMeaning;
import com.tqt.englishApp.enums.Level;
import com.tqt.englishApp.repository.UserLearningProfileRepository;
import com.tqt.englishApp.repository.VocabularyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlacementTestServiceTest {

    @InjectMocks
    private PlacementTestService service;

    @Mock
    private VocabularyRepository vocabularyRepository;

    @Mock
    private QuizGenerateService quizGenerateService;

    @Mock
    private UserLearningProfileRepository userLearningProfileRepository;

    private UserLearningProfile profile;

    @BeforeEach
    void setUp() {
        profile = new UserLearningProfile();
        profile.setUserId("user-1");
        profile.setLevel(Level.A1);
    }

    // -----------------------------------------------------------------------
    // Helper
    // -----------------------------------------------------------------------
    private Vocabulary buildVocab(int id) {
        VocabularyMeaning meaning = new VocabularyMeaning();
        meaning.setId(id);

        Vocabulary vocab = new Vocabulary();
        vocab.setId(id);
        vocab.setMeanings(List.of(meaning));
        return vocab;
    }

    private QuizGenerateResponse buildQuizResponse(int id) {
        return QuizGenerateResponse.builder()
                .id(id)
                .question("What is word " + id + "?")
                .build();
    }

    // -----------------------------------------------------------------------
    // generatePlacementQuiz
    // -----------------------------------------------------------------------
    @Test
    void generatePlacementQuiz_UserNotFound_ThrowsException() {
        when(userLearningProfileRepository.findByUserId("ghost")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.generatePlacementQuiz("ghost"));
    }

    @Test
    void generatePlacementQuiz_A1Level_CorrectDistribution() {
        when(userLearningProfileRepository.findByUserId("user-1")).thenReturn(Optional.of(profile));

        // A1: easy=7, med=2, hard=1
        // easy → A1+A2: 4+3 vocabs; med → B1+B2: 1+1; hard → C1+C2: 1+0
        List<Vocabulary> easyVocabs = List.of(buildVocab(1), buildVocab(2), buildVocab(3),
                buildVocab(4), buildVocab(5), buildVocab(6), buildVocab(7));
        List<Vocabulary> medVocabs = List.of(buildVocab(8), buildVocab(9));
        List<Vocabulary> hardVocabs = List.of(buildVocab(10));

        when(vocabularyRepository.findRandomByLevel(eq(Level.A1.name()), anyInt()))
                .thenAnswer(inv -> easyVocabs.subList(0, Math.min((int) inv.getArgument(1), easyVocabs.size())));
        when(vocabularyRepository.findRandomByLevel(eq(Level.A2.name()), anyInt()))
                .thenAnswer(inv -> easyVocabs.subList(0, Math.min((int) inv.getArgument(1), easyVocabs.size())));
        when(vocabularyRepository.findRandomByLevel(eq(Level.B1.name()), anyInt()))
                .thenAnswer(inv -> medVocabs.subList(0, Math.min((int) inv.getArgument(1), medVocabs.size())));
        when(vocabularyRepository.findRandomByLevel(eq(Level.B2.name()), anyInt()))
                .thenAnswer(inv -> medVocabs.subList(0, Math.min((int) inv.getArgument(1), medVocabs.size())));
        when(vocabularyRepository.findRandomByLevel(eq(Level.C1.name()), anyInt()))
                .thenAnswer(inv -> hardVocabs.subList(0, Math.min((int) inv.getArgument(1), hardVocabs.size())));
        when(quizGenerateService.generateQuizEngToVn(anyInt())).thenReturn(buildQuizResponse(-1));

        PlacementQuizResponse result = service.generatePlacementQuiz("user-1");

        assertNotNull(result);
        assertEquals(Level.A1, result.getInitialLevel());
        assertFalse(result.getQuestions().isEmpty());
    }

    @Test
    void generatePlacementQuiz_VocabWithNoMeanings_Skipped() {
        profile.setLevel(Level.B1);
        when(userLearningProfileRepository.findByUserId("user-1")).thenReturn(Optional.of(profile));

        // Vocab không có meaning → bỏ qua
        Vocabulary noMeaning = new Vocabulary();
        noMeaning.setId(99);
        noMeaning.setMeanings(List.of());

        Vocabulary withMeaning = buildVocab(1);

        when(vocabularyRepository.findRandomByLevel(anyString(), anyInt()))
                .thenReturn(List.of(noMeaning, withMeaning));
        when(quizGenerateService.generateQuizEngToVn(anyInt())).thenReturn(buildQuizResponse(-1));

        PlacementQuizResponse result = service.generatePlacementQuiz("user-1");

        // noMeaning vocab bị bỏ qua
        result.getQuestions().forEach(q ->
                assertNotNull(q.getMeaningId(), "MeaningId should not be null for valid vocabs"));
    }

    @Test
    void generatePlacementQuiz_QuizIdsAreSequential() {
        when(userLearningProfileRepository.findByUserId("user-1")).thenReturn(Optional.of(profile));

        // Return a mutable QuizGenerateResponse so service can set the id
        when(vocabularyRepository.findRandomByLevel(anyString(), anyInt()))
                .thenReturn(List.of(buildVocab(1), buildVocab(2), buildVocab(3)));
        // Use a real response object (not pre-set id=-1) so setId() works
        when(quizGenerateService.generateQuizEngToVn(anyInt()))
                .thenAnswer(inv -> QuizGenerateResponse.builder().id(-1).question("Q?").build());

        PlacementQuizResponse result = service.generatePlacementQuiz("user-1");

        for (int i = 0; i < result.getQuestions().size(); i++) {
            assertEquals(i + 1, result.getQuestions().get(i).getQuiz().getId(),
                    "Quiz IDs should be sequential starting from 1");
        }
    }

    // -----------------------------------------------------------------------
    // processDiagnosticResults
    // -----------------------------------------------------------------------
    @Test
    void processDiagnosticResults_AllCorrect_RaisesLevelBy1() {
        profile.setLevel(Level.A2);
        when(userLearningProfileRepository.findByUserId("user-1")).thenReturn(Optional.of(profile));
        when(userLearningProfileRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        DiagnosticQuizRequest req = buildDiagnosticRequest("user-1", 10, 10); // 10/10

        service.processDiagnosticResults(req);

        // 10/10 → offset +1 → B1
        assertEquals(Level.B1, profile.getLevel());
    }

    @Test
    void processDiagnosticResults_AllWrong_SetsA1() {
        profile.setLevel(Level.B2);
        when(userLearningProfileRepository.findByUserId("user-1")).thenReturn(Optional.of(profile));
        when(userLearningProfileRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        DiagnosticQuizRequest req = buildDiagnosticRequest("user-1", 0, 10);

        service.processDiagnosticResults(req);

        assertEquals(Level.A1, profile.getLevel());
    }

    @Test
    void processDiagnosticResults_B2Level_6Correct_StaysB2() {
        profile.setLevel(Level.B2);
        when(userLearningProfileRepository.findByUserId("user-1")).thenReturn(Optional.of(profile));
        when(userLearningProfileRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        DiagnosticQuizRequest req = buildDiagnosticRequest("user-1", 6, 10);

        service.processDiagnosticResults(req);

        assertEquals(Level.B2, profile.getLevel());
    }

    @Test
    void processDiagnosticResults_B2Level_4Correct_DropsToB1() {
        profile.setLevel(Level.B2);
        when(userLearningProfileRepository.findByUserId("user-1")).thenReturn(Optional.of(profile));
        when(userLearningProfileRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        DiagnosticQuizRequest req = buildDiagnosticRequest("user-1", 4, 10);

        service.processDiagnosticResults(req);

        assertEquals(Level.B1, profile.getLevel());
    }

    @Test
    void processDiagnosticResults_B2Level_3Correct_DropsToA2() {
        profile.setLevel(Level.B2);
        when(userLearningProfileRepository.findByUserId("user-1")).thenReturn(Optional.of(profile));
        when(userLearningProfileRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        DiagnosticQuizRequest req = buildDiagnosticRequest("user-1", 3, 10);

        service.processDiagnosticResults(req);

        assertEquals(Level.A2, profile.getLevel());
    }

    @Test
    void processDiagnosticResults_B1Level_6Correct_StaysB1() {
        profile.setLevel(Level.B1);
        when(userLearningProfileRepository.findByUserId("user-1")).thenReturn(Optional.of(profile));
        when(userLearningProfileRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        DiagnosticQuizRequest req = buildDiagnosticRequest("user-1", 6, 10);

        service.processDiagnosticResults(req);

        assertEquals(Level.B1, profile.getLevel());
    }

    @Test
    void processDiagnosticResults_A2Level_5Correct_StaysA2() {
        profile.setLevel(Level.A2);
        when(userLearningProfileRepository.findByUserId("user-1")).thenReturn(Optional.of(profile));
        when(userLearningProfileRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        DiagnosticQuizRequest req = buildDiagnosticRequest("user-1", 5, 10);

        service.processDiagnosticResults(req);

        assertEquals(Level.A2, profile.getLevel());
    }

    @Test
    void processDiagnosticResults_A2Level_4Correct_DropsToA1() {
        profile.setLevel(Level.A2);
        when(userLearningProfileRepository.findByUserId("user-1")).thenReturn(Optional.of(profile));
        when(userLearningProfileRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        DiagnosticQuizRequest req = buildDiagnosticRequest("user-1", 4, 10);

        service.processDiagnosticResults(req);

        assertEquals(Level.A1, profile.getLevel());
    }

    @Test
    void processDiagnosticResults_A1Level_AlwaysStaysA1() {
        profile.setLevel(Level.A1);
        when(userLearningProfileRepository.findByUserId("user-1")).thenReturn(Optional.of(profile));
        when(userLearningProfileRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        DiagnosticQuizRequest req = buildDiagnosticRequest("user-1", 7, 10);

        service.processDiagnosticResults(req);

        assertEquals(Level.A1, profile.getLevel());
    }

    @Test
    void processDiagnosticResults_UserNotFound_DoesNothing() {
        when(userLearningProfileRepository.findByUserId("ghost")).thenReturn(Optional.empty());

        DiagnosticQuizRequest req = buildDiagnosticRequest("ghost", 8, 10);

        assertDoesNotThrow(() -> service.processDiagnosticResults(req));
        verify(userLearningProfileRepository, never()).save(any());
    }

    // -----------------------------------------------------------------------
    // Helper
    // -----------------------------------------------------------------------
    private DiagnosticQuizRequest buildDiagnosticRequest(String userId, int correctCount, int total) {
        List<DiagnosticQuizRequest.WordTestResult> results = new java.util.ArrayList<>();
        for (int i = 0; i < total; i++) {
            DiagnosticQuizRequest.WordTestResult r = new DiagnosticQuizRequest.WordTestResult();
            r.setIsCorrect(i < correctCount);
            results.add(r);
        }
        DiagnosticQuizRequest req = new DiagnosticQuizRequest();
        req.setUserId(userId);
        req.setWordResults(results);
        return req;
    }
}
