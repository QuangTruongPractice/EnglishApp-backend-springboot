package com.tqt.englishApp.dto.response.vocabulary;

import com.tqt.englishApp.enums.Level;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VocabulariesSimpleResponse {
    Integer id;
    String word;
    String phonetic;
    Level level;
}
