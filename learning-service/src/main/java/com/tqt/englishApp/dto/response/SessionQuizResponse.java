package com.tqt.englishApp.dto.response;

import com.tqt.englishApp.dto.response.quiz.BaseQuizResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SessionQuizResponse {
    Integer id;
    BaseQuizResponse quiz;
    Integer meaningId;
    Boolean isCorrect;
    Integer xpAwarded;
}
