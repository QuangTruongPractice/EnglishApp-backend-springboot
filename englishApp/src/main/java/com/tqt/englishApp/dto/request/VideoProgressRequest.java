package com.tqt.englishApp.dto.request;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VideoProgressRequest {
    String userId;
    Integer videoId;
    Integer watchedDuration;
    Integer lastPosition;
    Integer videoDuration;
}
