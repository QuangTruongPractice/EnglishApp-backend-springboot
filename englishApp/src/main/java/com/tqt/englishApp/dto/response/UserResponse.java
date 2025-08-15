package com.tqt.englishApp.dto.response;

import com.tqt.englishApp.enums.Role;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String id;
    String username;
    String email;
    String firstName;
    String lastName;
    LocalDate dob;
    String avatar;
    LocalDate createdAt;
    Boolean isActive;
    Role role;
}
