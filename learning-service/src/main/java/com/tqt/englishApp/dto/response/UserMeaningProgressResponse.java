package com.tqt.englishApp.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserMeaningProgressResponse {
    String status;

    @JsonProperty("next_review_at")
    LocalDate nextReviewAt;
}
