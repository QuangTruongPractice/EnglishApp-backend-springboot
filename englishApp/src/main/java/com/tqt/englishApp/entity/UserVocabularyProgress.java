package com.tqt.englishApp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tqt.englishApp.enums.VocabularyStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_vocabulary_progress")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserVocabularyProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    User user;

    @ManyToOne
    @JoinColumn(name = "vocabulary_id")
    @JsonIgnore
    Vocabulary vocabulary;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    VocabularyStatus status = VocabularyStatus.NOT_STARTED;

    @Column(name = "viewed_flashcard")
    Boolean viewedFlashcard = false;

    @Column(name = "practiced_pronunciation")
    Boolean practicedPronunciation = false;

    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();

        if (viewedFlashcard && practicedPronunciation) {
            this.status = VocabularyStatus.COMPLETED;
        } else if (viewedFlashcard) {
            this.status = VocabularyStatus.IN_PROGRESS;
        } else if (practicedPronunciation) {
            this.status = VocabularyStatus.IN_PROGRESS;
        } else {
            this.status = VocabularyStatus.NOT_STARTED;
        }
    }
}
