package com.tqt.englishApp.mapper;

import com.tqt.englishApp.dto.response.SessionQuizResponse;
import com.tqt.englishApp.entity.SessionQuiz;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {QuizMapper.class, VocabularyMeaningMapper.class})
public interface SessionQuizMapper {
    @Mapping(target = "quiz", source = "quiz")
    @Mapping(target = "meaningId", source = "meaning.id")
    SessionQuizResponse toSessionQuizResponse(SessionQuiz sessionQuiz);

    List<SessionQuizResponse> toSessionQuizResponse(List<SessionQuiz> sessionQuizzes);
}

