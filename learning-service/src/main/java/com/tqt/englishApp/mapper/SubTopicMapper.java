package com.tqt.englishApp.mapper;

import com.tqt.englishApp.dto.request.SubTopicRequest;
import com.tqt.englishApp.dto.response.subTopic.SubTopicsAdminResponse;
import com.tqt.englishApp.dto.response.subTopic.SubTopicsDetailResponse;
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

    public abstract SubTopicsAdminResponse toSubTopicsAdminResponse(SubTopic subTopic);

    public abstract List<SubTopicsAdminResponse> toSubTopicsAdminResponse(List<SubTopic> subTopics);

    public abstract SubTopicsDetailResponse toSubTopicsDetailResponse(SubTopic subTopic);

    @Mapping(target = "mainTopic", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateSubTopic(@MappingTarget SubTopic subTopic, SubTopicRequest request);

    @AfterMapping
    protected void setVocabularyCount(@MappingTarget Object response, SubTopic subTopic) {
        if (subTopicRepository != null) {
            Long count = subTopicRepository.countVocabularyBySubTopicId(subTopic.getId());
            if (response instanceof SubTopicsAdminResponse) {
                ((SubTopicsAdminResponse) response).setVocabularyCount(count);
            } else if (response instanceof SubTopicsDetailResponse) {
                ((SubTopicsDetailResponse) response).setVocabularyCount(count);
            }
        }
    }

}
