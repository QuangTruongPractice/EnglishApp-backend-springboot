package com.tqt.englishApp.dto.response;

import com.tqt.englishApp.enums.LearningGoal;
import com.tqt.englishApp.enums.Level;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserLearningProfileResponse {
    Integer id;
    String userId;
    Level level;
    Integer dailyTarget;
    Integer xp;
    Integer weeklyXp;
    Integer totalXp;
    LearningGoal goal;
    Boolean onboardingCompleted;
    LocalDate createdAt;
    LocalDate profileUpdatedAt;
    Integer currentStreak;
    Integer longestStreak;
}
