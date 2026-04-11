package com.tqt.englishApp.dto.response;

import com.tqt.englishApp.dto.response.quiz.BaseQuizResponse;
import com.tqt.englishApp.enums.Level;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlacementQuizResponse {
    Level initialLevel;
    List<PlacementQuestion> questions;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class PlacementQuestion {
        BaseQuizResponse quiz;
        Integer meaningId;
    }
}
