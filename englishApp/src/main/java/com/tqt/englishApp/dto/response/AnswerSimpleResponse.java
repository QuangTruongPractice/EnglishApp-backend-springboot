package com.tqt.englishApp.dto.response;

import com.tqt.englishApp.entity.Quiz;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnswerSimpleResponse {
    Integer id;
    String answer;
    Boolean isCorrect;
    LocalDate createdAt;
}

