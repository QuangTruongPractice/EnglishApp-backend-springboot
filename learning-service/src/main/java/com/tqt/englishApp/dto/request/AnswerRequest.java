package com.tqt.englishApp.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnswerRequest {
    Integer id;
    @NotBlank(message = "Câu trả lời không được để trống")
    String answer;
    Boolean isCorrect;
    Integer quiz;
}
