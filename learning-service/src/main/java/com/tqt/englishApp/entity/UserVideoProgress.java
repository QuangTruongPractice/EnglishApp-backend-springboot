package com.tqt.englishApp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_video_progress", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "user_id", "video_id" })
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserVideoProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "user_id")
    String userId;

    @ManyToOne
    @JoinColumn(name = "video_id")
    @JsonIgnore
    Video video;

    @Builder.Default
    @Column(name = "watched_duration")
    Integer watchedDuration = 0;

    @Builder.Default
    @Column(name = "progress_percentage")
    Double progressPercentage = 0.0;

    @Builder.Default
    @Column(name = "last_position")
    Integer lastPosition = 0;

    @Builder.Default
    @Column(name = "is_completed")
    Boolean isCompleted = false;

    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    public void updateProgress() {
        this.updatedAt = LocalDateTime.now();

        if (this.isCompleted == null) {
            this.isCompleted = false;
        }

        if (this.progressPercentage != null && this.progressPercentage >= 90.0) {
            this.isCompleted = true;
        }
    }
}
