package com.tqt.englishApp.entity;

import com.tqt.englishApp.enums.MatchSide;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "match_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MatchItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "quiz_id", nullable = false)
    Quiz quiz;

    @Enumerated(EnumType.STRING)
    @Column(name = "side")
    MatchSide side;

    @Column(name = "pair_key")
    String pairKey;

    @Column(name = "content", columnDefinition = "TEXT")
    String content;

    @Column(name = "order_index")
    Integer orderIndex;
}
