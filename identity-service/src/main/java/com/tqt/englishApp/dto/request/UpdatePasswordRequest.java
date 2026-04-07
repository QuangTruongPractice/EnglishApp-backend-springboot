package com.tqt.englishApp.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdatePasswordRequest {
    @Size(min = 6, message = "PASSWORD_INVALID")
    String oldPassword;
    @Size(min = 6, message = "PASSWORD_INVALID")
    String newPassword;
}
