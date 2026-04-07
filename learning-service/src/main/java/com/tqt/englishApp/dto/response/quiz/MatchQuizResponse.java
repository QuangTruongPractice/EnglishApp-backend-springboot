package com.tqt.englishApp.dto.response.quiz;

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
public class MatchQuizResponse extends BaseQuizResponse {
    List<MatchItemResponse> left_items;
    List<MatchItemResponse> right_items;
}

