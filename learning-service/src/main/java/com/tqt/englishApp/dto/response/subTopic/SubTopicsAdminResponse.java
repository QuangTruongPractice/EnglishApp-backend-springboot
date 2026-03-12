package com.tqt.englishApp.dto.response.subTopic;

import com.tqt.englishApp.dto.response.vocabulary.VocabulariesSimpleResponse;
import com.tqt.englishApp.dto.response.mainTopic.MainTopicsSimpleResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubTopicsAdminResponse {
    Integer id;
    String name;
    Long vocabularyCount;
    MainTopicsSimpleResponse mainTopic;
    List<VocabulariesSimpleResponse> vocabularies;
    LocalDate createdAt;
}
