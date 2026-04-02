package com.tqt.englishApp.mapper;

import com.tqt.englishApp.dto.request.AnswerRequest;
import com.tqt.englishApp.dto.response.AnswerResponse;
import com.tqt.englishApp.dto.response.AnswerSimpleResponse;
import com.tqt.englishApp.entity.Answer;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AnswerMapper {
    @Mapping(target = "quiz", ignore = true)
    @Mapping(target = "text", ignore = true)
    @Mapping(target = "meaningId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Answer toAnswer(AnswerRequest answer);

    AnswerResponse toAnswerResponse(Answer answer);

    AnswerSimpleResponse toAnswerSimpleResponse(Answer answer);

    @Mapping(target = "quiz", source = "quiz.id")
    AnswerRequest toAnswerRequest(Answer answer);

    @Mapping(target = "quiz", ignore = true)
    @Mapping(target = "text", ignore = true)
    @Mapping(target = "meaningId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateAnswer(@MappingTarget Answer answer, AnswerRequest request);
}
