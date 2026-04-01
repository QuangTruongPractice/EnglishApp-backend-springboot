package com.tqt.englishApp.dto.response;

import com.tqt.englishApp.enums.Type;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VocabularyMeaningResponse {
    Integer id;
    Type type;
    String definition;
    String vnWord;
    String vnDefinition;
    String example;
    String vnExample;
    List<String> synonyms;
    List<String> images;
}
