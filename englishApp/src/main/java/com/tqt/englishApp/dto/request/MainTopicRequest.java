package com.tqt.englishApp.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MainTopicRequest {
    Integer id;
    @NotBlank(message = "Tên topic không được để trống")
    String name;
    MultipartFile image;
    LocalDate createdAt;
}
