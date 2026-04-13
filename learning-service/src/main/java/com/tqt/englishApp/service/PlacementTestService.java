package com.tqt.englishApp.service;

import com.tqt.englishApp.dto.request.DiagnosticQuizRequest;
import com.tqt.englishApp.dto.response.PlacementQuizResponse;
import com.tqt.englishApp.dto.response.quiz.QuizGenerateResponse;
import com.tqt.englishApp.entity.Vocabulary;
import com.tqt.englishApp.entity.VocabularyMeaning;
import com.tqt.englishApp.entity.UserLearningProfile;
import com.tqt.englishApp.enums.Level;
import com.tqt.englishApp.repository.UserLearningProfileRepository;
import com.tqt.englishApp.repository.VocabularyRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PlacementTestService {

    VocabularyRepository vocabularyRepository;
    QuizGenerateService quizGenerateService;
    UserLearningProfileRepository userLearningProfileRepository;
    Random random = new Random();

    public PlacementQuizResponse generatePlacementQuiz(String userId) {
        UserLearningProfile profile = userLearningProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User learning profile not found"));
        
        Level selectedLevel = profile.getLevel();
        int easyCount, medCount, hardCount;

        switch (selectedLevel) {
            case A1:
                easyCount = 7; medCount = 2; hardCount = 1;
                break;
            case A2:
                easyCount = 5; medCount = 3; hardCount = 2;
                break;
            case B1:
                easyCount = 3; medCount = 4; hardCount = 3;
                break;
            case B2:
                easyCount = 2; medCount = 4; hardCount = 4;
                break;
            default:
                easyCount = 5; medCount = 3; hardCount = 2;
                break;
        }

        List<Vocabulary> vocabList = new ArrayList<>();
        vocabList.addAll(fetchRandomVocabs(List.of(Level.A1, Level.A2), easyCount));
        vocabList.addAll(fetchRandomVocabs(List.of(Level.B1, Level.B2), medCount));
        vocabList.addAll(fetchRandomVocabs(List.of(Level.C1, Level.C2), hardCount));

        List<PlacementQuizResponse.PlacementQuestion> questions = new ArrayList<>();
        int quizIdCounter = 1;
        for (Vocabulary vocab : vocabList) {
            if (vocab.getMeanings().isEmpty()) continue;
            
            VocabularyMeaning meaning = vocab.getMeanings().get(0);
            QuizGenerateResponse quizRes = quizGenerateService.generateQuizEngToVn(meaning.getId());
            quizRes.setId(quizIdCounter++);
            
            questions.add(PlacementQuizResponse.PlacementQuestion.builder()
                    .quiz(quizRes)
                    .meaningId(meaning.getId())
                    .build());
        }

        return PlacementQuizResponse.builder()
                .initialLevel(selectedLevel)
                .questions(questions)
                .build();
    }

    private List<Vocabulary> fetchRandomVocabs(List<Level> levels, int count) {
        if (count <= 0) return new ArrayList<>();
        List<Vocabulary> result = new ArrayList<>();
        int perLevel = Math.max(1, count / levels.size());
        
        for (Level lvl : levels) {
            int limit = Math.min(count - result.size(), perLevel);
            if (limit <= 0) break;
            result.addAll(vocabularyRepository.findRandomByLevel(lvl.name(), limit));
        }
        
        if (result.size() < count) {
            result.addAll(vocabularyRepository.findRandomByLevel(levels.get(0).name(), count - result.size()));
        }
        
        return result;
    }

    @Transactional
    public void processDiagnosticResults(DiagnosticQuizRequest request) {
        long correctCount = request.getWordResults().stream()
                .filter(DiagnosticQuizRequest.WordTestResult::getIsCorrect)
                .count();

        userLearningProfileRepository.findByUserId(request.getUserId()).ifPresent(profile -> {
            Level currentLevel = profile.getLevel();
            if (currentLevel == null) currentLevel = Level.A1;
            
            Level recommendedLevel = currentLevel;

            if (correctCount == 0) {
                // Thất bại hoàn toàn: Xuống A1
                recommendedLevel = Level.A1;
            } else if (correctCount == 10) {
                // Làm rất tốt: Tăng 1 bậc (Ví dụ: B2 chọn ban đầu sẽ lên C1)
                recommendedLevel = currentLevel.getOffsetLevel(1);
            } else {
                // Logic tăng giảm dựa trên cấp độ hiện tại (Aggrressive Decrease)
                if (currentLevel == Level.B2) {
                    if (correctCount >= 6) {
                        recommendedLevel = Level.B2; 
                    } else if (correctCount >= 4) {
                        recommendedLevel = Level.B1; 
                    } else {
                        recommendedLevel = Level.A2;
                    }
                } else if (currentLevel == Level.B1) {
                    if (correctCount >= 6) {
                        recommendedLevel = Level.B1;
                    } else if (correctCount >= 4) {
                        recommendedLevel = Level.A2; 
                    } else {
                        recommendedLevel = Level.A1;
                    }
                } else if (currentLevel == Level.A2) {
                    if (correctCount >= 5) {
                        recommendedLevel = Level.A2;
                    } else {
                        recommendedLevel = Level.A1; 
                    }
                } else if (currentLevel == Level.A1) {
                    recommendedLevel = Level.A1;
                }
            }

            profile.setLevel(recommendedLevel);
            userLearningProfileRepository.save(profile);
        });
    }
}
