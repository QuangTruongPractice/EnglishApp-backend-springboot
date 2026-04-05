package com.tqt.englishApp.dto.response.subTopic;

import com.tqt.englishApp.dto.response.mainTopic.UserTopicProgressResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubTopicsSimpleResponse {
    Integer id;
    String name;
    LocalDate createdAt;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("user_progress")
    UserTopicProgressResponse userProgress;
}
