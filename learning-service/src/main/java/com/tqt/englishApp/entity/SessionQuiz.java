package com.tqt.englishApp.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "session_quiz")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SessionQuiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "session_id", nullable = false)
    Session session;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "quiz_id", nullable = false)
    Quiz quiz;

    @ManyToOne
    @JoinColumn(name = "meaning_id", nullable = false)
    VocabularyMeaning meaning;

    @Column(name = "is_correct")
    Boolean isCorrect;

    @Column(name = "xp_awarded")
    Integer xpAwarded;

    @Column(name = "user_answer", columnDefinition = "TEXT")
    String userAnswer;

    @Column(name = "answered_at")
    java.time.LocalDateTime answeredAt;

    @Column(name = "retry_attempt")
    @Builder.Default
    Integer retryAttempt = 0;

    @PrePersist
    public void prePersist() {
        this.answeredAt = java.time.LocalDateTime.now();
    }
}
