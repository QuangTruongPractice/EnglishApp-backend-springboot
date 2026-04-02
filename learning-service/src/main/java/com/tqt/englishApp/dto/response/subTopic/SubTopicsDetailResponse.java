package com.tqt.englishApp.dto.response.subTopic;

import com.tqt.englishApp.dto.response.vocabulary.VocabulariesSimpleResponse;
import com.tqt.englishApp.dto.response.mainTopic.UserTopicProgressResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubTopicsDetailResponse {
    Integer id;
    String name;
    Long vocabularyCount;
    List<VocabulariesSimpleResponse> vocabularies;
    LocalDate createdAt;

    @JsonProperty("user_progress")
    UserTopicProgressResponse userProgress;
}
