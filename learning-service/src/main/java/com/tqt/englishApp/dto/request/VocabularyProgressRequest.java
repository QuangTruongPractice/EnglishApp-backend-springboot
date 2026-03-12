package com.tqt.englishApp.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VocabularyProgressRequest {
    String userId;
    Integer meaningId;
    Boolean isCorrect;
}
