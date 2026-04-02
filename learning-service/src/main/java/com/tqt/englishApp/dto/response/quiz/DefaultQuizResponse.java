package com.tqt.englishApp.dto.response.quiz;

import com.tqt.englishApp.dto.response.AnswerSimpleResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DefaultQuizResponse extends BaseQuizResponse {
    List<AnswerSimpleResponse> answers;
}
