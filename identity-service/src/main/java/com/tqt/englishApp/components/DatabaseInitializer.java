package com.tqt.englishApp.components;

import com.tqt.englishApp.entity.Permission;
import com.tqt.englishApp.entity.Role;
import com.tqt.englishApp.entity.User;
import com.tqt.englishApp.repository.PermissionRepository;
import com.tqt.englishApp.repository.RoleRepository;
import com.tqt.englishApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Create Permissions
        Permission userApproval = createPermissionIfNotExist("USER_APPROVAL", "Quyền duyệt người dùng");
        Permission viewStats = createPermissionIfNotExist("VIEW_STATS", "Quyền xem thống kê");
        Permission manageVocabulary = createPermissionIfNotExist("MANAGE_VOCABULARY", "Quyền quản lý từ vựng");
        Permission manageLesson = createPermissionIfNotExist("MANAGE_LESSON", "Quyền quản lý bài học");
        Permission manageStaff = createPermissionIfNotExist("MANAGE_STAFF", "Quyền quản lý nhân viên (staff)");

        // Create Roles
        Role adminRole = createRoleIfNotExist("ADMIN", "Quản trị viên toàn quyền",
                new HashSet<>(Set.of(userApproval, viewStats, manageVocabulary, manageLesson, manageStaff)));
        Role staffRole = createRoleIfNotExist("STAFF", "Nhân viên quản lý nội dung và người dùng",
                new HashSet<>(Set.of(userApproval, viewStats, manageVocabulary, manageLesson)));
        Role userRole = createRoleIfNotExist("USER", "Người dùng thông thường", new HashSet<>());

        // Create Admin User
        User admin = userRepository.findUserByUsername("admin");
        if (admin != null) {
            if (admin.getRoles() == null || admin.getRoles().isEmpty()) {
                admin.setRoles(new HashSet<>(Set.of(adminRole)));
                userRepository.save(admin);
                log.info("Assigned roles to existing admin user");
            } else {
                log.info("Admin user already exists with roles");
            }
        } else {
            User newAdmin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("123456"))
                    .email("admin@gmail.com")
                    .firstName("Admin")
                    .lastName("System")
                    .dob(LocalDate.of(2000, 1, 1))
                    .isActive(true)
                    .roles(new HashSet<>(Set.of(adminRole)))
                    .build();

            userRepository.save(newAdmin);
            log.info("Admin user created successfully with username: admin");
        }
    }

    private Permission createPermissionIfNotExist(String name, String description) {
        return permissionRepository.findById(name).orElseGet(() -> {
            Permission permission = Permission.builder()
                    .name(name)
                    .description(description)
                    .build();
            log.info("Created permission: {}", name);
            return permissionRepository.save(permission);
        });
    }

    private Role createRoleIfNotExist(String name, String description, Set<Permission> permissions) {
        return roleRepository.findById(name).orElseGet(() -> {
            Role role = Role.builder()
                    .name(name)
                    .description(description)
                    .permissions(permissions)
                    .build();
            log.info("Created role: {}", name);
            return roleRepository.save(role);
        });
    }
}
