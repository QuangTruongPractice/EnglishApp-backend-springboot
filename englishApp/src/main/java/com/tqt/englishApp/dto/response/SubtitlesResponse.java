package com.tqt.englishApp.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubtitlesResponse {
    Float confidence;
    Float startTime;
    Float endTime;
    String originalText;
    Integer segmentId;
}
