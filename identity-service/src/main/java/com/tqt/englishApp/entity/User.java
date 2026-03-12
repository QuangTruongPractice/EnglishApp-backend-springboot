package com.tqt.englishApp.entity;

import java.util.Set;
import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String username;
    String password;
    String email;
    String firstName;
    String lastName;
    LocalDate dob;
    String avatar;
    LocalDate createdAt;
    Boolean isActive;
    @ManyToMany
    Set<Role> roles;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDate.now();
    }
}
