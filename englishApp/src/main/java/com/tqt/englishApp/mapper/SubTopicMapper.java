package com.tqt.englishApp.mapper;

import com.tqt.englishApp.dto.request.SubTopicRequest;
import com.tqt.englishApp.dto.response.SubTopicResponse;
import com.tqt.englishApp.entity.SubTopic;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SubTopicMapper {
    @Mapping(target = "mainTopic", ignore = true)
    SubTopic toSubTopic(SubTopicRequest subTopic);
    SubTopicResponse toSubTopicResponse(SubTopic subTopic);
    List<SubTopicResponse> toSubTopicResponse(List<SubTopic> subTopics);
    @Mapping(target = "mainTopic", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateSubTopic(@MappingTarget SubTopic subTopic, SubTopicRequest request);
}
