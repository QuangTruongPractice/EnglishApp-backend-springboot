package com.tqt.englishApp.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WeeklyLeaderboardResponse {
    String userId;
    String username; // From auth-service or placeholder
    Integer weeklyXp;
    Integer rank;
    String level;
}
