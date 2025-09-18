package com.tqt.englishApp.mapper;

import com.tqt.englishApp.dto.request.MainTopicRequest;
import com.tqt.englishApp.dto.request.VocabularyRequest;
import com.tqt.englishApp.dto.response.MainTopicResponse;
import com.tqt.englishApp.dto.response.VocabularyResponse;
import com.tqt.englishApp.entity.MainTopic;
import com.tqt.englishApp.entity.Vocabulary;
import org.mapstruct.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VocabularyMapper {
    @Mapping(target = "subTopics", ignore = true)
    @Mapping(target = "wordTypes", ignore = true)
    @Mapping(target = "audioUrl", ignore = true)
    Vocabulary toVocabulary(VocabularyRequest vocabulary);
    VocabularyResponse toVocabularyResponse(Vocabulary vocabulary);
    @Mapping(target = "subTopics", ignore = true)
    @Mapping(target = "wordTypes", ignore = true)
    @Mapping(target = "audioUrl", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateVocabulary(@MappingTarget Vocabulary vocabulary, VocabularyRequest request);
}
