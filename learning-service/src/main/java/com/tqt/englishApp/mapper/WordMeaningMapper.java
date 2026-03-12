package com.tqt.englishApp.mapper;

import com.tqt.englishApp.dto.request.WordMeaningRequest;
import com.tqt.englishApp.dto.response.WordMeaningResponse;
import com.tqt.englishApp.entity.MeaningImage;
import com.tqt.englishApp.entity.MeaningSynonym;
import com.tqt.englishApp.entity.WordMeaning;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface WordMeaningMapper {

    @Mapping(target = "synonyms", source = "synonyms", qualifiedByName = "mapSynonyms")
    @Mapping(target = "images", source = "images", qualifiedByName = "mapImages")
    WordMeaningResponse toWordMeaningResponse(WordMeaning meaning);

    List<WordMeaningResponse> toWordMeaningResponse(List<WordMeaning> meanings);

    @Mapping(target = "existingImageUrls", source = "images")
    @Mapping(target = "imageFiles", ignore = true)
    @Mapping(target = "synonymWords", source = "synonyms")
    WordMeaningRequest toWordMeaningRequest(WordMeaningResponse response);

    List<WordMeaningRequest> toWordMeaningRequest(List<WordMeaningResponse> responses);

    @Named("mapSynonyms")
    default List<String> mapSynonyms(List<MeaningSynonym> synonyms) {
        if (synonyms == null)
            return null;
        return synonyms.stream()
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
