package com.tqt.englishApp.mapper;

import com.tqt.englishApp.dto.request.VideoRequest;
import com.tqt.englishApp.dto.response.VideoResponse;
import com.tqt.englishApp.entity.Video;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface VideoMapper {
    Video toVideo(VideoResponse videoResponse);
    VideoResponse toVideoResponse(Video video);
}
