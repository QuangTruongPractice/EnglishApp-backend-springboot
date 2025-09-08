package com.tqt.englishApp.dto.request;

import com.tqt.englishApp.enums.VocabularyStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VocabularyProgressRequest {
    String userId;
    Integer vocabularyId;
    Boolean viewedFlashcard = false;
    Boolean practicedPronunciation = false;
}
