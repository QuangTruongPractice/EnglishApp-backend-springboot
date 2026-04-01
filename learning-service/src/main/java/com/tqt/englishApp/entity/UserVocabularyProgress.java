package com.tqt.englishApp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tqt.englishApp.enums.VocabularyStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_vocabulary_progress", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "user_id", "meaning_id" })
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserVocabularyProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "user_id", nullable = false)
    String userId;

    @ManyToOne
    @JoinColumn(name = "meaning_id", nullable = false)
    VocabularyMeaning meaning;

    @Builder.Default
    @Column(name = "repetition_count")
    Integer repetitionCount = 0;

    @Builder.Default
    @Column(name = "interval_day")
    Integer intervalDay = 1;

    @Builder.Default
    @Column(name = "ease_factor")
    Double easeFactor = 2.5;

    @Column(name = "next_review_at")
    LocalDateTime nextReviewAt;

    @Column(name = "last_reviewed_at")
    LocalDateTime lastReviewedAt;

    @Builder.Default
    @Column(name = "correct_count")
    Integer correctCount = 0;

    @Builder.Default
    @Column(name = "wrong_count")
    Integer wrongCount = 0;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    VocabularyStatus status = VocabularyStatus.NOT_STARTED;

    @Column(name = "updated_at")
    LocalDateTime updatedAt;
}
