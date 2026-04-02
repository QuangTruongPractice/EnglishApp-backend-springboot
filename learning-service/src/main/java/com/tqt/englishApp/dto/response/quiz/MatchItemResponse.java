package com.tqt.englishApp.dto.response.quiz;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MatchItemResponse {
    String id;
    String word;  // Only in left_items
    String text;  // Only in right_items
}
