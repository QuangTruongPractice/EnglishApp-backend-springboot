package com.tqt.englishApp.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WeeklyLeaderboardWrapperResponse {
    List<WeeklyLeaderboardResponse> leaderBoard;
    WeeklyLeaderboardResponse currentUser;
}
