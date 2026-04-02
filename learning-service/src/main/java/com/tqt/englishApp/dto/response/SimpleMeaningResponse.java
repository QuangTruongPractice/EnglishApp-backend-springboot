package com.tqt.englishApp.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SimpleMeaningResponse {
    Integer id;
    String word;
}
