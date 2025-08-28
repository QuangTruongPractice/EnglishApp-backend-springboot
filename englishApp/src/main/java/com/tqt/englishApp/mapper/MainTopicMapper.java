package com.tqt.englishApp.mapper;

import com.tqt.englishApp.dto.request.MainTopicRequest;
import com.tqt.englishApp.dto.response.MainTopicResponse;
import com.tqt.englishApp.entity.MainTopic;
import com.tqt.englishApp.repository.MainTopicRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class MainTopicMapper {

    @Autowired
    protected MainTopicRepository mainTopicRepository;

    @Mapping(target = "image", ignore = true)
    public abstract MainTopic toMainTopic(MainTopicRequest mainTopic);

    public abstract MainTopicResponse toMainTopicResponse(MainTopic mainTopic);

    public abstract List<MainTopicResponse> toMainTopicResponse(List<MainTopic> mainTopics);

    @Mapping(target = "image", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateMainTopic(@MappingTarget MainTopic mainTopic, MainTopicRequest request);

    protected String mapMultipartFile(MultipartFile file) {
        return null;
    }

    @AfterMapping
    protected void setSubTopicsCount(@MappingTarget MainTopicResponse response, MainTopic mainTopic) {
        if (mainTopicRepository != null) {
            Long count = mainTopicRepository.countSubTopicsByMainTopicId(mainTopic.getId());
            response.setSubTopicsCount(count);
        }
    }

}
