package com.tqt.englishApp.entity;

import com.tqt.englishApp.enums.Type;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Table(name = "word_meaning")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WordMeaning {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "vocabulary_id", nullable = false)
    Vocabulary vocabulary;

    @Enumerated(EnumType.STRING)
    Type type;

    @Column(name = "definition")
    String definition;

    @Column(name = "vn_word")
    String vnWord;

    @Column(name = "vn_definition")
    String vnDefinition;

    @Column(name = "example")
    String example;

    @Column(name = "vn_example")
    String vnExample;

    @OneToMany(mappedBy = "meaning", cascade = CascadeType.ALL, orphanRemoval = true)
    List<MeaningSynonym> synonyms;

    @OneToMany(mappedBy = "meaning", cascade = CascadeType.ALL, orphanRemoval = true)
    List<MeaningImage> images;
}
