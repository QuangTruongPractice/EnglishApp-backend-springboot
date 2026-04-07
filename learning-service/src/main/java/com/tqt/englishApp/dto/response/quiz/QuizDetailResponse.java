package com.tqt.englishApp.dto.response.quiz;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuizDetailResponse {
    Integer id;
    String question;
    String text;
    QuizType type;
    List<AnswerSimpleResponse> answers;
    List<MatchItemResponse> left_items;
    List<MatchItemResponse> right_items;
    LocalDateTime createdAt;
}
