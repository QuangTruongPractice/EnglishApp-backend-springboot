package com.tqt.englishApp.mapper;

import com.tqt.englishApp.dto.request.QuizRequest;
import com.tqt.englishApp.dto.response.quiz.BaseQuizResponse;
import com.tqt.englishApp.dto.response.quiz.DefaultQuizResponse;
import com.tqt.englishApp.dto.response.quiz.MatchItemResponse;
import com.tqt.englishApp.dto.response.quiz.MatchQuizResponse;
import com.tqt.englishApp.dto.response.quiz.QuizDetailResponse;
import com.tqt.englishApp.entity.Quiz;
import com.tqt.englishApp.entity.MatchItem;
import com.tqt.englishApp.enums.MatchSide;
import com.tqt.englishApp.enums.QuizType;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring", uses = {AnswerMapper.class})
public interface QuizMapper {
    @Mapping(target = "answers", ignore = true)
    @Mapping(target = "matchItems", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Quiz toQuiz(QuizRequest quiz);

    default BaseQuizResponse toQuizResponse(Quiz quiz) {
        if (quiz == null) return null;
        if (quiz.getType() == QuizType.MATCH) {
            return toMatchQuizResponse(quiz);
        } else {
            return toDefaultQuizResponse(quiz);
        }
    }

    @Mapping(target = "left_items", ignore = true)
    @Mapping(target = "right_items", ignore = true)
    MatchQuizResponse toMatchQuizResponse(Quiz quiz);

    DefaultQuizResponse toDefaultQuizResponse(Quiz quiz);

    QuizDetailResponse toQuizDetailResponse(Quiz quiz);

    @Mapping(target = "answers", ignore = true)
    @Mapping(target = "matchItems", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateQuiz(@MappingTarget Quiz quiz, QuizRequest request);

    @AfterMapping
    default void populateMatchItems(@MappingTarget MatchQuizResponse response, Quiz quiz) {
        if (quiz.getMatchItems() != null && !quiz.getMatchItems().isEmpty()) {
            List<MatchItemResponse> left = new ArrayList<>();
            List<MatchItemResponse> right = new ArrayList<>();

            quiz.getMatchItems().forEach(item -> {
                if (item.getSide() == MatchSide.LEFT) {
                    left.add(MatchItemResponse.builder()
                            .id(item.getPairKey())
                            .word(item.getContent())
                            .build());
                } else {
                    right.add(MatchItemResponse.builder()
                            .id(item.getPairKey())
                            .text(item.getContent())
                            .build());
                }
            });

            Collections.shuffle(right);
            response.setLeft_items(left);
            response.setRight_items(right);
        }
    }
}
