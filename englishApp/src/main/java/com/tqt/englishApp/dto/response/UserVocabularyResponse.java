package com.tqt.englishApp.dto.response;

import com.tqt.englishApp.enums.VocabularyStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserVocabularyResponse {
    Integer id;
    VocabularyResponse vocabulary;
    VocabularyStatus status;
    LocalDateTime updatedAt;
}
