package com.tqt.englishApp.mapper;

import com.tqt.englishApp.dto.request.VocabularyRequest;
import com.tqt.englishApp.dto.response.vocabulary.VocabulariesResponse;
import com.tqt.englishApp.dto.response.vocabulary.VocabulariesSimpleResponse;
import com.tqt.englishApp.entity.Vocabulary;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = { WordMeaningMapper.class })
public interface VocabularyMapper {
    @Mapping(target = "subTopics", ignore = true)
    @Mapping(target = "audioUrl", ignore = true)
    @Mapping(target = "meanings", ignore = true)
    Vocabulary toVocabulary(VocabularyRequest vocabulary);

    VocabulariesResponse toVocabulariesResponse(Vocabulary vocabulary);

    VocabulariesSimpleResponse toVocabulariesSimpleResponse(Vocabulary vocabulary);

    @Mapping(target = "audioFile", ignore = true)
    @Mapping(target = "subTopics", ignore = true)
    @Mapping(target = "meanings", ignore = true)
    @Mapping(target = "audioUrl", source = "audioUrl")
    VocabularyRequest toVocabularyRequest(VocabulariesResponse response);

    @Mapping(target = "subTopics", ignore = true)
    @Mapping(target = "audioUrl", ignore = true)
    @Mapping(target = "meanings", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateVocabulary(@MappingTarget Vocabulary vocabulary, VocabularyRequest request);

}
