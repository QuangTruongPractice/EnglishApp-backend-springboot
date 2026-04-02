package com.tqt.englishApp.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SessionResponse {
    Integer id;
    String userId;
    LocalDate date;
    List<VocabularyMeaningResponse> meanings;
    List<SessionQuizResponse> quizzes;
    List<WritingPromptResponse> writingPrompts;
    Integer totalXP;
    Boolean completed;
    Boolean isLevelUp;
}
