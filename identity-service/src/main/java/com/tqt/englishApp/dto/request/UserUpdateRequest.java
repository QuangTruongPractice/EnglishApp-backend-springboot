package com.tqt.englishApp.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
    @Pattern(regexp = "^$|.{6,}", message = "PASSWORD_INVALID")
    String password;
    String firstName;
    String lastName;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate dob;
    MultipartFile avatar;
    Boolean isActive;
    Set<String> roles;
}
