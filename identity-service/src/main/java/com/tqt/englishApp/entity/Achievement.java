package com.tqt.englishApp.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "achievement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Achievement {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(unique = true, nullable = false)
    String code;

    String title;
    String description;

    @Column(length = 1000)
    String requirementText;

    String category;
    Integer progressTarget;
    Integer rewardGems;
    Integer rewardXp;
    String badgeIcon;
    String badgeColor;
    String badgeBorderColor;
    String badgeGlowColor;
}
