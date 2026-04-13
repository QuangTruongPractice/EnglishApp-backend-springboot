package com.tqt.englishApp.entity;

import com.tqt.englishApp.enums.LearningGoal;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "main_topic")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE main_topic SET is_deleted = true, deleted_at = NOW() WHERE id = ?")
@Where(clause = "is_deleted = false")
public class MainTopic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "name")
    String name;

    @Column(name = "image")
    String image;

    @Enumerated(EnumType.STRING)
    @Column(name = "goal")
    LearningGoal goal;

    @Column(name = "topic_order")
    Integer topicOrder;

    @OneToMany(mappedBy = "mainTopic", cascade = CascadeType.ALL, orphanRemoval = true)
    List<SubTopic> subTopics;

    @Column(name = "created_at", updatable = false)
    LocalDate createdAt;

    @Builder.Default
    @Column(name = "is_deleted", nullable = false)
    Boolean isDeleted = false;

    @Column(name = "deleted_at")
    LocalDateTime deletedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDate.now();
    }
}
