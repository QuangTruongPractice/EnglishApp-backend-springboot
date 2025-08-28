package com.tqt.englishApp.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MainTopicResponse {
    Integer id;
    String name;
    String image;
    Long subTopicsCount;
    List<SubTopicSimpleResponse> subTopics;
    LocalDate createdAt;
}
