package com.tqt.englishApp.entity;

import com.tqt.englishApp.enums.Level;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "vocabulary")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE vocabulary SET is_deleted = true, deleted_at = NOW() WHERE id = ?")
@Where(clause = "is_deleted = false")
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

    @ManyToMany
    @JoinTable(name = "vocabulary_subtopic", joinColumns = @JoinColumn(name = "vocabulary_id"), inverseJoinColumns = @JoinColumn(name = "sub_topic_id"))
    @Builder.Default
    List<SubTopic> subTopics = new ArrayList<>();

    @OneToMany(mappedBy = "vocabulary", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    List<VocabularyMeaning> meanings = new ArrayList<>();

    @Builder.Default
    @Column(name = "is_deleted", nullable = false)
    Boolean isDeleted = false;

    @Column(name = "deleted_at")
    LocalDateTime deletedAt;
}
