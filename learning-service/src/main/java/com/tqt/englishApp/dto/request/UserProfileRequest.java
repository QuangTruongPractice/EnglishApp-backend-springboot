package com.tqt.englishApp.dto.request;

import com.tqt.englishApp.enums.LearningGoal;
import com.tqt.englishApp.enums.Level;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfileRequest {
    Level level;
    Integer dailyTarget;
    LearningGoal goal;
}
