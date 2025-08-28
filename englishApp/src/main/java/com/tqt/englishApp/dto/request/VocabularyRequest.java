package com.tqt.englishApp.dto.request;

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
public class VocabularyRequest {
    Integer id;
    String phonetic;
    String word;
    String definition;
    String vnWord;
    String vnDefinition;
    String example;
    String vnExample;
    String audioUrl;
    List<Integer> subTopics;
    List<Integer> wordTypes;
}
