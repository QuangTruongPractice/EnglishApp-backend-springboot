package com.tqt.englishApp.mapper;

import com.tqt.englishApp.dto.response.VideoResponse;
import com.tqt.englishApp.entity.Video;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VideoMapper {
    Video toVideo(VideoResponse videoResponse);
    VideoResponse toVideoResponse(Video video);
}
