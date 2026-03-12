package com.tqt.englishApp.dto.request;

import com.tqt.englishApp.enums.Level;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VocabularyRequest {
    Integer id;
    @NotBlank(message = "Phiên âm không được để trống")
    String phonetic;
    @NotBlank(message = "Từ vựng không được để trống")
    String word;
    Level level;
    String audioUrl;
    MultipartFile audioFile;
    List<Integer> subTopics;
    List<WordMeaningRequest> meanings;
}
