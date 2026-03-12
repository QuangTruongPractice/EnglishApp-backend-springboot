package com.tqt.englishApp.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "meaning_synonym")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MeaningSynonym {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "meaning_id", nullable = false)
    WordMeaning meaning;

    @ManyToOne
    @JoinColumn(name = "synonym_meaning_id", nullable = false)
    WordMeaning synonymMeaning;
}
