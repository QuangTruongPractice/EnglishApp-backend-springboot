package com.tqt.englishApp.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserVideoResponse {
    Integer id;
    VideoResponse video;
    Integer progressPercentage;
    Boolean isCompleted;
    LocalDateTime updatedAt;
}
