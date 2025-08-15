package com.tqt.englishApp.dto.response;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.tqt.englishApp.entity.MainTopic;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubTopicResponse {
    Integer id;
    String name;
    MainTopicSimpleResponse mainTopic;
    List<VocabularySimpleResponse> vocabularies;
    LocalDate createdAt;
}
