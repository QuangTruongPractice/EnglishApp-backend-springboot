package com.tqt.englishApp.dto.response.mainTopic;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MainTopicsSimpleResponse {
    String name;
}
