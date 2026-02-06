package com.tqt.englishApp.dto.request;

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
