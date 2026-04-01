package com.tqt.englishApp.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MatchQuizResponse {
    Integer matchId;
    List<MatchPair> pairs;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class MatchPair {
        Integer quizId;
        String word;
        String definition;
    }
}
