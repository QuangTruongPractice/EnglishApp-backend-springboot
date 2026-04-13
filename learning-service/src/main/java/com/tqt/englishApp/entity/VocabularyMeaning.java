package com.tqt.englishApp.entity;

import com.tqt.englishApp.enums.Type;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "vocabulary_meaning")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE vocabulary_meaning SET is_deleted = true, deleted_at = NOW() WHERE id = ?")
@Where(clause = "is_deleted = false")
public class VocabularyMeaning {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "vocabulary_id", nullable = false)
    Vocabulary vocabulary;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 50)
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

    @Builder.Default
    @Column(name = "is_deleted", nullable = false)
    Boolean isDeleted = false;

    @Column(name = "deleted_at")
    LocalDateTime deletedAt;
}
