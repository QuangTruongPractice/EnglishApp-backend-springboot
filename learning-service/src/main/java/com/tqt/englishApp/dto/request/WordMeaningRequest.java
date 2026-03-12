package com.tqt.englishApp.dto.request;

import com.tqt.englishApp.enums.Type;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WordMeaningRequest {
    Integer id;
    @NotNull(message = "Loại từ không được để trống")
    Type type;
    String definition;
    @NotBlank(message = "Nghĩa tiếng Việt không được để trống")
    String vnWord;
    String vnDefinition;
    String example;
    String vnExample;
    List<MultipartFile> imageFiles;
    List<String> existingImageUrls;
    List<String> synonymWords;
}
