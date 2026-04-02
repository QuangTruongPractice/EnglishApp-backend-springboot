package com.tqt.englishApp.mapper;

import com.tqt.englishApp.dto.response.vocabulary.UserVocabularyResponse;
import com.tqt.englishApp.entity.UserVocabularyProgress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserVocabularyMapper {
    @Mapping(target = "word", source = "meaning.vocabulary.word")
    @Mapping(target = "phonetic", source = "meaning.vocabulary.phonetic")
    @Mapping(target = "level", source = "meaning.vocabulary.level")
    @Mapping(target = "id", source = "meaning.vocabulary.id")
    @Mapping(target = "isSave", ignore = true)
    UserVocabularyResponse toUserVocabularyResponse(UserVocabularyProgress userVocabularyProgress);

    List<UserVocabularyResponse> toUserVocabularyResponse(List<UserVocabularyProgress> userVocabularyProgress);
}
