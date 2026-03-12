package com.tqt.englishApp.entity;

import com.tqt.englishApp.enums.Level;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vocabulary")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Vocabulary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "phonetic")
    String phonetic;

    @Column(name = "word")
    String word;

    @Enumerated(EnumType.STRING)
    @Column(name = "level")
    Level level;

    @Column(name = "audio_url")
    String audioUrl;

    @Column(name = "is_save")
    @Builder.Default
    Boolean isSave = false;

    @ManyToMany
    @JoinTable(name = "vocabulary_subtopic", joinColumns = @JoinColumn(name = "vocabulary_id"), inverseJoinColumns = @JoinColumn(name = "sub_topic_id"))
    @Builder.Default
    List<SubTopic> subTopics = new ArrayList<>();

    @OneToMany(mappedBy = "vocabulary", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    List<WordMeaning> meanings = new ArrayList<>();
}
