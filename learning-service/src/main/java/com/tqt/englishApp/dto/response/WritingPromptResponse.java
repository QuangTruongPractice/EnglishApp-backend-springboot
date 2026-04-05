package com.tqt.englishApp.dto.response;

import com.tqt.englishApp.enums.WritingPromptType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WritingPromptResponse {
    Integer id;
    WritingPromptType type;
    String question;
    List<SimpleMeaningResponse> meanings;
    String userResponse;
    Integer score;
    String improvedSentence;
    Boolean completed;
}
