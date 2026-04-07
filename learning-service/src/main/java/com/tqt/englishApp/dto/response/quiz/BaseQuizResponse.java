package com.tqt.englishApp.dto.response.quiz;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.tqt.englishApp.enums.QuizType;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type",
    visible = true
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = DefaultQuizResponse.class, names = {"MC", "FILL"}),
    @JsonSubTypes.Type(value = MatchQuizResponse.class, name = "MATCH")
})
public abstract class BaseQuizResponse {
    Integer id;
    String question;
    String text;
    QuizType type;
    LocalDateTime createdAt;
}
