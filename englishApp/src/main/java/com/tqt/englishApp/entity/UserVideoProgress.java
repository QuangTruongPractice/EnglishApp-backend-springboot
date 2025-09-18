package com.tqt.englishApp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_video_progress",
        uniqueConstraints = {
            @UniqueConstraint(columnNames = {"user_id", "video_id"})
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserVideoProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    User user;

    @ManyToOne
    @JoinColumn(name = "video_id")
    @JsonIgnore
    Video video;

    @Column(name = "watched_duration")
    Integer watchedDuration = 0;

    @Column(name = "progress_percentage")
    Double progressPercentage = 0.0;

    @Column(name = "last_position")
    Integer lastPosition = 0;

    @Column(name = "is_completed")
    Boolean isCompleted = false;

    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    public void updateProgress() {
        this.updatedAt = LocalDateTime.now();

        if (this.progressPercentage >= 90.0) {
            this.isCompleted = true;
        }
    }
}
