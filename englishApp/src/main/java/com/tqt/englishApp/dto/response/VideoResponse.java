package com.tqt.englishApp.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
}
