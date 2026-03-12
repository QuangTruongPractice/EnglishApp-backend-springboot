package com.tqt.englishApp.dto.response.quiz;

import com.tqt.englishApp.dto.response.AnswerSimpleResponse;
import com.tqt.englishApp.enums.QuizType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuizGenerateResponse {
    Integer id;
    String question;
    String text;
    QuizType type;
    List<AnswerSimpleResponse> answers;
    Integer meanId;
    LocalDateTime createdAt;
}
