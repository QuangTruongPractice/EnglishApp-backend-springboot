package com.tqt.englishApp.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "session")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "user_id", nullable = false)
    String userId;

    @Column(name = "date", nullable = false)
    LocalDate date;

    @ManyToMany
    @JoinTable(
        name = "session_meaning",
        joinColumns = @JoinColumn(name = "session_id"),
        inverseJoinColumns = @JoinColumn(name = "meaning_id")
    )
    List<VocabularyMeaning> meanings;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Quiz> quizzes;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    List<WritingPrompt> writingPrompts;

    @Column(name = "total_xp")
    @Builder.Default
    Integer totalXP = 0;

    @Column(name = "completed")
    @Builder.Default
    Boolean completed = false;

    @Column(name = "is_level_up")
    @Builder.Default
    Boolean isLevelUp = false;
}
