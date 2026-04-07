package com.tqt.englishApp.dto.response.mainTopic;

import lombok.*;
import lombok.experimental.FieldDefaults;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MainTopicsResponse {
    Integer id;
    String name;
    String image;
    Long subTopicsCount;

    @JsonProperty("user_progress")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    UserTopicProgressResponse userProgress;
}
