package com.tqt.englishApp.dto.response.mainTopic;

import com.tqt.englishApp.dto.response.subTopic.SubTopicsSimpleResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MainTopicsDetailResponse {
    Integer id;
    String name;
    String image;
    Long subTopicsCount;
    List<SubTopicsSimpleResponse> subTopics;
    LocalDate createdAt;
}
