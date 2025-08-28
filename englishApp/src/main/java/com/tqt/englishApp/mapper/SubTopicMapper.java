package com.tqt.englishApp.mapper;

import com.tqt.englishApp.dto.request.SubTopicRequest;
import com.tqt.englishApp.dto.response.MainTopicResponse;
import com.tqt.englishApp.dto.response.SubTopicResponse;
import com.tqt.englishApp.dto.response.SubTopicSimpleResponse;
import com.tqt.englishApp.entity.MainTopic;
import com.tqt.englishApp.entity.SubTopic;
import com.tqt.englishApp.repository.SubTopicRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class SubTopicMapper {

    @Autowired
    protected SubTopicRepository subTopicRepository;

    @Mapping(target = "mainTopic", ignore = true)
    public abstract SubTopic toSubTopic(SubTopicRequest subTopic);

    public abstract SubTopicResponse toSubTopicResponse(SubTopic subTopic);

    public abstract List<SubTopicResponse> toSubTopicResponse(List<SubTopic> subTopics);

    @Mapping(target = "mainTopic", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateSubTopic(@MappingTarget SubTopic subTopic, SubTopicRequest request);

    @AfterMapping
    protected void setVocabularyCount(@MappingTarget SubTopicResponse response, SubTopic subTopic) {
        if (subTopicRepository != null) {
            Long count = subTopicRepository.countVocabularyBySubTopicId(subTopic.getId());
            response.setVocabularyCount(count);
        }
    }

}
