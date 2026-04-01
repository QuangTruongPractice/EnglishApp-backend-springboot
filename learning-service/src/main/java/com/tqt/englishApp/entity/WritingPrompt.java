package com.tqt.englishApp.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

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

    @ManyToMany
    @JoinTable(
        name = "writing_prompt_meaning",
        joinColumns = @JoinColumn(name = "writing_prompt_id"),
        inverseJoinColumns = @JoinColumn(name = "meaning_id")
    )
    List<VocabularyMeaning> meanings;

    @Column(name = "user_response", columnDefinition = "TEXT")
    String userResponse;

    @ManyToOne
    @JoinColumn(name = "session_id")
    Session session;
}
