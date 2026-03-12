package com.tqt.englishApp.dto.request;

import com.tqt.englishApp.enums.QuizType;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuizRequest {
    Integer id;
    @NotBlank(message = "Câu hỏi không được để trống")
    String question;
    String text;
    QuizType type;
}
