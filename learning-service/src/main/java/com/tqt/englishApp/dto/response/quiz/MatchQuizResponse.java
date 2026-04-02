package com.tqt.englishApp.dto.response.quiz;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
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

    // Hide irrelevant fields from MATCH response
    @Override
    @JsonIgnore
    public String getQuestion() { return super.getQuestion(); }

    @Override
    @JsonIgnore
    public String getText() { return super.getText(); }

    @Override
    @JsonIgnore
    public LocalDateTime getCreatedAt() { return super.getCreatedAt(); }
}
