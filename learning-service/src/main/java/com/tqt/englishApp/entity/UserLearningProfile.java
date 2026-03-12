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
    @Column(name = "level")
    Level level;

    @Column(name = "daily_target")
    Integer dailyTarget;

    @Enumerated(EnumType.STRING)
    LearningGoal goal;

    @Column(name = "onboarding_completed")
    Boolean onboardingCompleted;

    @Column(name = "current_topic_id")
    Integer currentTopicId;

    @Column(name = "created_at", updatable = false)
    LocalDate createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDate.now();
    }
}
