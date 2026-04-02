package com.tqt.englishApp.dto.response.mainTopic;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserTopicProgressResponse {
    Long meanings_learned;
    Long meanings_total;
    Integer pct;
    String status;
}
