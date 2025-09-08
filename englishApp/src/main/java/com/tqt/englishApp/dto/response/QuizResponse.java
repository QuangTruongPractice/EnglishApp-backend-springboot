package com.tqt.englishApp.dto.response;

import com.tqt.englishApp.enums.QuizType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuizResponse {
    Integer id;
    String question;
    String text;
    QuizType type;
    List<AnswerSimpleResponse> answers;
    LocalDate createdAt;
}
