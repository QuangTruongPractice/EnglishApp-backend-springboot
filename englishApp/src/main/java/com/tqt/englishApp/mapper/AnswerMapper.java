package com.tqt.englishApp.mapper;

import com.tqt.englishApp.dto.request.AnswerRequest;
import com.tqt.englishApp.dto.response.AnswerResponse;
import com.tqt.englishApp.entity.Answer;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AnswerMapper {
    @Mapping(target = "quiz", ignore = true)
    Answer toAnswer(AnswerRequest answer);
    AnswerResponse toAnswerResponse(Answer answer);
    @Mapping(target = "quiz", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateAnswer(@MappingTarget Answer answer, AnswerRequest request);
}
