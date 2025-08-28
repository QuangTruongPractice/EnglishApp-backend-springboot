package com.tqt.englishApp.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "video")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "video_id", unique = true, nullable = false, length = 50)
    private String videoId;

    @Column(name = "title", nullable = false, length = 500)
    private String title;

    @Column(name = "youtube_url", nullable = false, columnDefinition = "TEXT")
    private String youtubeUrl;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "language", length = 10)
    private String language = "en";

    @Column(nullable = false)
    String status;

    @Column(name = "segments_count")
    Integer segmentsCount;

    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Subtitles> subtitles;

    @Column(name = "created_at", updatable = false)
    LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

}
