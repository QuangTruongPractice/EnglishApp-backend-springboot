package com.tqt.englishApp.mapper;

import com.tqt.englishApp.dto.request.MainTopicRequest;
import com.tqt.englishApp.dto.response.MainTopicResponse;
import com.tqt.englishApp.entity.MainTopic;
import org.mapstruct.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MainTopicMapper {
    @Mapping(target = "image", ignore = true)
    MainTopic toMainTopic(MainTopicRequest mainTopic);
    MainTopicResponse toMainTopicResponse(MainTopic mainTopic);
    List<MainTopicResponse> toMainTopicResponse(List<MainTopic> mainTopics);
    @Mapping(target = "image", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateMainTopic(@MappingTarget MainTopic mainTopic, MainTopicRequest request);

    default String mapMultipartFile(MultipartFile file) {
        return null;
    }
}
