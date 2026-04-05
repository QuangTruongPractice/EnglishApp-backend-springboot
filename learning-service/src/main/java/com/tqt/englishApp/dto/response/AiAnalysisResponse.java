package com.tqt.englishApp.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AiAnalysisResponse {
    Integer score;
    List<TargetKeyword> target_keywords;
    String improved_sentence;
    Double processing_time;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class TargetKeyword {
        String keyword;
        Boolean correct_usage;
        String reason;
    }
}
