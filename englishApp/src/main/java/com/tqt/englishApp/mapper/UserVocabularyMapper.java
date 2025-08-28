package com.tqt.englishApp.mapper;

import com.tqt.englishApp.dto.response.UserVocabularyResponse;
import com.tqt.englishApp.entity.UserVocabularyProgress;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserVocabularyMapper {
    List<UserVocabularyResponse> toUserVocabularyResponse(List<UserVocabularyProgress> userVocabularyProgress);
}
