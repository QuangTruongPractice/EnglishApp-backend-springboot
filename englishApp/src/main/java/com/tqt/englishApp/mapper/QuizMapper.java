package com.tqt.englishApp.mapper;

import com.tqt.englishApp.dto.request.QuizRequest;
import com.tqt.englishApp.dto.response.QuizResponse;
import com.tqt.englishApp.entity.Quiz;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface QuizMapper {
    Quiz toQuiz(QuizRequest quiz);
    QuizResponse toQuizResponse(Quiz quiz);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateQuiz(@MappingTarget Quiz quiz, QuizRequest request);
}
