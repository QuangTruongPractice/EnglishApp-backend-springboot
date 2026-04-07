package com.tqt.englishApp.entity;

import com.tqt.englishApp.enums.LearningGoal;
import com.tqt.englishApp.enums.Level;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Entity
@Table(name = "user_learning_profile")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserLearningProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "user_id")
    String userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "level", length = 50)
    Level level;

    @Column(name = "daily_target")
    Integer dailyTarget;

    @Column(name = "xp")
    @Builder.Default
    Integer xp = 0;

    @Column(name = "weekly_xp")
    @Builder.Default
    Integer weeklyXp = 0;

    @Column(name = "total_xp")
    @Builder.Default
    Integer totalXp = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "goal", length = 50)
    LearningGoal goal;

    @Column(name = "onboarding_completed")
    @Builder.Default
    Boolean onboardingCompleted = false;

    @Column(name = "created_at", updatable = false)
    LocalDate createdAt;

    @Column(name = "profile_updated_at")
    LocalDate profileUpdatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDate.now();
    }
}
