package com.tqt.englishApp.dto.response.quiz;

import com.tqt.englishApp.enums.QuizType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

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
    LocalDateTime createdAt;
}
