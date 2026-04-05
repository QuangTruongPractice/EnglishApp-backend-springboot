package com.tqt.englishApp.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VideoResponse {
    Integer id;
    String videoId;
    String title;
    String youtubeUrl;
    Integer duration;
    String language;
    String status;
    Integer segmentsCount;
    LocalDateTime createdAt;
    Double progressPercentage;
    Boolean isCompleted;
}
