package com.tqt.englishApp.mapper;

import com.tqt.englishApp.dto.request.MainTopicRequest;
import com.tqt.englishApp.dto.response.mainTopic.MainTopicsAdminResponse;
import com.tqt.englishApp.dto.response.mainTopic.MainTopicsDetailResponse;
import com.tqt.englishApp.dto.response.mainTopic.MainTopicsResponse;
import com.tqt.englishApp.entity.MainTopic;
import com.tqt.englishApp.repository.MainTopicRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class MainTopicMapper {

    @Autowired
    protected MainTopicRepository mainTopicRepository;

    @Mapping(target = "image", ignore = true)
    public abstract MainTopic toMainTopic(MainTopicRequest request);

    public abstract MainTopicsAdminResponse toMainTopicsAdminResponse(MainTopic mainTopic);

    public abstract List<MainTopicsAdminResponse> toMainTopicsAdminResponse(List<MainTopic> mainTopics);

    public abstract MainTopicsResponse toMainTopicsResponse(MainTopic mainTopic);

    public abstract List<MainTopicsResponse> toMainTopicsResponse(List<MainTopic> mainTopics);

    public abstract MainTopicsDetailResponse toMainTopicsDetailResponse(MainTopic mainTopic);

    @Mapping(target = "image", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateMainTopic(@MappingTarget MainTopic mainTopic, MainTopicRequest request);

    @AfterMapping
    protected void setSubTopicsCount(@MappingTarget Object response, MainTopic mainTopic) {
        if (mainTopicRepository != null) {
            Long count = mainTopicRepository.countSubTopicsByMainTopicId(mainTopic.getId());
            if (response instanceof MainTopicsAdminResponse) {
                ((MainTopicsAdminResponse) response).setSubTopicsCount(count);
            } else if (response instanceof MainTopicsResponse) {
                ((MainTopicsResponse) response).setSubTopicsCount(count);
            } else if (response instanceof MainTopicsDetailResponse) {
                ((MainTopicsDetailResponse) response).setSubTopicsCount(count);
            }
        }
    }

}
