package com.tqt.englishApp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

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
    String phonetic;
    String word;
    String definition;
    String vnWord;
    String vnDefinition;
    String example;
    String vnExample;
    String audioUrl;

    @ManyToMany
    @JoinTable(
            name = "vocabulary_subtopic",
            joinColumns = @JoinColumn(name = "vocabulary_id"),
            inverseJoinColumns = @JoinColumn(name = "sub_topic_id")
    )
    List<SubTopic> subTopics;

    @ManyToMany
    @JoinTable(
            name = "vocabulary_wordtype",
            joinColumns = @JoinColumn(name = "vocabulary_id"),
            inverseJoinColumns = @JoinColumn(name = "wordtype_id")
    )
    List<WordType> wordTypes;
}
