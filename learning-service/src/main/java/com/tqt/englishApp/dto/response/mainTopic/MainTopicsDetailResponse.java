package com.tqt.englishApp.dto.response.mainTopic;

import com.tqt.englishApp.dto.response.subTopic.SubTopicsSimpleResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MainTopicsDetailResponse {
    Integer id;
    String name;
    String image;
    Long subTopicsCount;
    List<SubTopicsSimpleResponse> subTopics;
    LocalDate createdAt;

    @JsonProperty("user_progress")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    UserTopicProgressResponse userProgress;
}
