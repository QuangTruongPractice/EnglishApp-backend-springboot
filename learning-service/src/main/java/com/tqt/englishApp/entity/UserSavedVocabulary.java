package com.tqt.englishApp.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_saved_vocabulary", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "vocabulary_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserSavedVocabulary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "user_id", nullable = false)
    String userId;

    @ManyToOne
    @JoinColumn(name = "vocabulary_id", nullable = false)
    Vocabulary vocabulary;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
