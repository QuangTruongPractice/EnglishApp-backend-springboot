package com.tqt.englishApp.mapper;

import com.tqt.englishApp.dto.response.SessionResponse;
import com.tqt.englishApp.entity.Session;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {VocabularyMeaningMapper.class, SessionQuizMapper.class, WritingPromptMapper.class})
public interface SessionMapper {
    @Mapping(target = "meanings", source = "meanings")
    @Mapping(target = "quizzes", source = "quizzes")
    @Mapping(target = "writingPrompts", source = "writingPrompts")
    SessionResponse toSessionResponse(Session session);
}
