package com.tqt.englishApp.entity;

import com.tqt.englishApp.enums.WritingPromptType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "writing_prompt")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WritingPrompt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    WritingPromptType type;

    @Column(name = "target_meaning_ids")
    String targetMeaningIds;

    @Column(name = "user_response", columnDefinition = "TEXT")
    String userResponse;

    @Column(name = "score")
    Integer score;

    @Column(name = "improved_sentence", columnDefinition = "TEXT")
    String improvedSentence;


    @Column(name = "completed")
    @Builder.Default
    Boolean completed = false;

    @ManyToOne
    @JoinColumn(name = "session_id")
    Session session;
}
