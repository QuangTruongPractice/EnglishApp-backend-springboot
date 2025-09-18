package com.tqt.englishApp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_quiz_progress",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "quiz_id"})
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserQuizProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    User user;

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    @JsonIgnore
    Quiz quiz;

    Integer count = 0;

    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    public void updateProgress() {
        this.updatedAt = LocalDateTime.now();
    }
}