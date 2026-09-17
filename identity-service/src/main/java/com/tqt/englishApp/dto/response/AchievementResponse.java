package com.tqt.englishApp.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AchievementResponse {
    String id;
    String code;
    String title;
    String description;
    String requirementText;
    String category;
    Integer progressCurrent;
    Integer progressTarget;
    Integer rewardGems;
    Integer rewardXp;
    String badgeIcon;
    String badgeColor;
    String badgeBorderColor;
    String badgeGlowColor;
    String status; // "LOCKED", "CLAIMABLE", "CLAIMED"
}
