package com.tqt.englishApp.dto.response.subTopic;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubTopicsSimpleResponse {
    Integer id;
    String name;
    LocalDate createdAt;
}
