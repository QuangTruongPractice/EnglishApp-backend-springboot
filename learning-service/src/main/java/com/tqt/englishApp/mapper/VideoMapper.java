package com.tqt.englishApp.mapper;

import com.tqt.englishApp.dto.response.VideoResponse;
import com.tqt.englishApp.entity.Video;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VideoMapper {
    Video toVideo(VideoResponse videoResponse);

    @Mapping(target = "youtubeUrl", expression = "java(\"https://www.youtube.com/watch?v=\" + video.getVideoId())")
    VideoResponse toVideoResponse(Video video);
}
