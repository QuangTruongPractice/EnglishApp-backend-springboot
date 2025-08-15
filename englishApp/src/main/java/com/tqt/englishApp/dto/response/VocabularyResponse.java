package com.tqt.englishApp.dto.response;

import com.tqt.englishApp.entity.SubTopic;
import com.tqt.englishApp.entity.WordType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VocabularyResponse {
    Integer id;
    String phonetic;
    String word;
    String definition;
    String vnWord;
    String vnDefinition;
    String example;
    String vnExample;
    String audioUrl;
    List<SubTopicSimpleResponse> subTopics;
    List<WordType> wordTypes;
}
