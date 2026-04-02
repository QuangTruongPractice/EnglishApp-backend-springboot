package com.tqt.englishApp.dto.response.subTopic;

import com.tqt.englishApp.dto.response.mainTopic.UserTopicProgressResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubTopicsSimpleResponse {
    Integer id;
    String name;
    LocalDate createdAt;

    @JsonProperty("user_progress")
    UserTopicProgressResponse userProgress;
}
