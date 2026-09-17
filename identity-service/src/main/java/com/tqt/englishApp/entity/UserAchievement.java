package com.tqt.englishApp.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_achievement", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"userId", "achievementCode"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAchievement {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String userId;
    String achievementCode;
    Integer progressCurrent = 0;
    String status = "LOCKED"; // LOCKED, CLAIMABLE, CLAIMED
    LocalDateTime unlockedAt;
    LocalDateTime claimedAt;
}
