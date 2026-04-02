package com.tqt.englishApp.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LearningSummaryResponse {
    int streak;
    int totalXP;
    long learningCount;
    long masteredCount;
    long videoCount;
    int weeklyRank;
    long savedVocabularyCount;
}
