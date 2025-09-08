package com.tqt.englishApp.dto.request;

import com.tqt.englishApp.dto.response.QuizResponse;
import com.tqt.englishApp.entity.Quiz;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnswerRequest {
    Integer id;
    String answer;
    Boolean isCorrect;
    Integer quiz;
}
