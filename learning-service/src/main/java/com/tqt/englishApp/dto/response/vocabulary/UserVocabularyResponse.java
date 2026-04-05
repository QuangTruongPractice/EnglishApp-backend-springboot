package com.tqt.englishApp.dto.response.vocabulary;

import com.tqt.englishApp.enums.VocabularyStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserVocabularyResponse extends VocabulariesSimpleResponse {
    VocabularyStatus status;
    LocalDateTime nextReviewAt;
}
