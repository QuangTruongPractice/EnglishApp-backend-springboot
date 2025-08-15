package com.tqt.englishApp.dto.response;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    List<SubTopicResponse> subTopics;
    LocalDate createdAt;
}
