package com.tqt.englishApp.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_avatar_frame", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"userId", "frameKey"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAvatarFrame {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String userId;
    String frameKey;
    LocalDateTime unlockedAt;

    @PrePersist
    public void prePersist() {
        this.unlockedAt = LocalDateTime.now();
    }
}
