package com.tqt.englishApp.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DiagnosticQuizRequest {
    String userId;
    List<WordTestResult> wordResults;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class WordTestResult {
        Integer meaningId;
        Boolean isCorrect;
        Long responseTimeMs;
    }
}
