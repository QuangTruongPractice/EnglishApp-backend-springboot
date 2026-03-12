package com.tqt.englishApp.dto.response.subTopic;

import com.tqt.englishApp.dto.response.vocabulary.VocabulariesSimpleResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

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
}
