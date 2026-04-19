package com.tqt.englishApp.mapper;

import com.tqt.englishApp.dto.request.VocabularyMeaningRequest;
import com.tqt.englishApp.dto.response.VocabularyMeaningResponse;
import com.tqt.englishApp.entity.MeaningImage;
import com.tqt.englishApp.entity.MeaningSynonym;
import com.tqt.englishApp.entity.VocabularyMeaning;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface VocabularyMeaningMapper {

    @Mapping(target = "synonyms", source = "synonyms", qualifiedByName = "mapSynonyms")
    @Mapping(target = "images", source = "images", qualifiedByName = "mapImages")
    @Mapping(target = "word", source = "vocabulary.word")
    @Mapping(target = "phonetic", source = "vocabulary.phonetic")
    @Mapping(target = "level", source = "vocabulary.level")
    @Mapping(target = "audioUrl", source = "vocabulary.audioUrl")
    VocabularyMeaningResponse toVocabularyMeaningResponse(VocabularyMeaning meaning);

    List<VocabularyMeaningResponse> toVocabularyMeaningResponse(List<VocabularyMeaning> meanings);

    @Mapping(target = "existingImageUrls", source = "images")
    @Mapping(target = "imageFiles", ignore = true)
    @Mapping(target = "synonymWords", source = "synonyms")
    VocabularyMeaningRequest toVocabularyMeaningRequest(VocabularyMeaningResponse response);

    List<VocabularyMeaningRequest> toVocabularyMeaningRequest(List<VocabularyMeaningResponse> responses);

    @Named("mapSynonyms")
    default List<String> mapSynonyms(List<MeaningSynonym> synonyms) {
        if (synonyms == null)
            return null;
        return synonyms.stream()
                .filter(s -> s.getSynonymMeaning() != null && 
                             s.getSynonymMeaning().getVocabulary() != null)
                .map(s -> s.getSynonymMeaning().getVocabulary().getWord())
                .collect(Collectors.toList());
    }

    @Named("mapImages")
    default List<String> mapImages(List<MeaningImage> images) {
        if (images == null)
            return null;
        return images.stream()
                .map(MeaningImage::getUrl)
                .collect(Collectors.toList());
    }
}
