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
public class AnswerResponse {
    Integer id;
    String answer;
    Boolean isCorrect;
    Quiz quiz;
    LocalDate createdAt;
}
