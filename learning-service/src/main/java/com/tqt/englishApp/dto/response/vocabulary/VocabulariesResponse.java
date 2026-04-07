package com.tqt.englishApp.dto.response.vocabulary;

import com.tqt.englishApp.dto.response.VocabularyMeaningResponse;
import com.tqt.englishApp.dto.response.subTopic.SubTopicsSimpleResponse;
import com.tqt.englishApp.enums.Level;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VocabulariesResponse {
    Integer id;
    String phonetic;
    String word;
    Level level;
    String audioUrl;
    List<SubTopicsSimpleResponse> subTopics;
    List<VocabularyMeaningResponse> meanings;
    LocalDateTime createdAt;
}
