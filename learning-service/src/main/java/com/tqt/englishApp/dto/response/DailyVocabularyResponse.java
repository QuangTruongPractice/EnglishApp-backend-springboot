package com.tqt.englishApp.dto.response;

import com.tqt.englishApp.dto.response.vocabulary.VocabulariesResponse;

import com.tqt.englishApp.enums.VocabularyStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
public class DailyVocabularyResponse extends VocabulariesResponse {
    Boolean isReview;
    VocabularyStatus status;
}
