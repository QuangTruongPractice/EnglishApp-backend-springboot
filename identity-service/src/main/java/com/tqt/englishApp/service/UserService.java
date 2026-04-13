package com.tqt.englishApp.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.tqt.englishApp.dto.request.*;
import com.tqt.englishApp.dto.response.UserResponse;
import com.tqt.englishApp.entity.User;
import com.tqt.englishApp.entity.Role;
import com.tqt.englishApp.exception.AppException;
import com.tqt.englishApp.exception.ErrorCode;
import com.tqt.englishApp.mapper.UserMapper;
import com.tqt.englishApp.repository.RoleRepository;
import com.tqt.englishApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private OtpService otpService;

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private MailService mailService;

    private static final int PAGE_SIZE = 10;

    public UserResponse createUser(UserCreationRequest request) {
        MultipartFile avatar = request.getAvatar();

        if (avatar == null || avatar.isEmpty()) {
            throw new AppException(ErrorCode.AVATAR_REQUIRED);
        }
        if (userRepository.existsUserByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USERNAME_EXISTED);
        }
        if (userRepository.existsUserByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setIsActive(true);

        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            Set<com.tqt.englishApp.entity.Role> roles = new HashSet<>(roleRepository.findAllById(request.getRoles()));
            user.setRoles(roles);
        } else {
            roleRepository.findById("USER").ifPresent(role -> user.setRoles(new HashSet<>(Set.of(role))));
        }

        try {
            Map res = cloudinary.uploader().upload(
                    avatar.getBytes(),
                    ObjectUtils.asMap("resource_type", "auto"));
            user.setAvatar(res.get("secure_url").toString());
        } catch (IOException ex) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
        return userMapper.toUserResponse(userRepository.save(user));
    }

    public UserResponse updateUser(String userId, UserUpdateRequest request) {
        MultipartFile avatar = request.getAvatar();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        userMapper.updateUser(user, request);
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getRoles() != null) {
            Set<com.tqt.englishApp.entity.Role> roles = new HashSet<>(roleRepository.findAllById(request.getRoles()));
            user.setRoles(roles);
        }

        if (avatar != null && !avatar.isEmpty()) {
            try {
                Map res = cloudinary.uploader().upload(
                        avatar.getBytes(),
                        ObjectUtils.asMap("resource_type", "auto"));
                user.setAvatar(res.get("secure_url").toString());
            } catch (IOException ex) {
                throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
            }
        }
        return userMapper.toUserResponse(userRepository.save(user));
    }

    public Page<UserResponse> getUsers(Map<String, String> params) {
        String keyword = params.get("keyword");
        int page = Integer.parseInt(params.getOrDefault("page", "1")) - 1;
        int size = Integer.parseInt(params.getOrDefault("size", String.valueOf(PAGE_SIZE)));

        page = Math.max(0, page);

        Pageable pageable = PageRequest.of(page, size);
        Page<User> result;
        if (keyword != null && !keyword.isBlank()) {
            result = userRepository.searchByEmailUsernameOrFullName(keyword, pageable);
        } else {
            result = userRepository.findAll(pageable);
        }
        return result.map(userMapper::toUserResponse);
    }

    public UserResponse getUserById(String id) {
        return userMapper.toUserResponse(
                userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
    }

    public UserResponse findUserByUsername(String username) {
        User u = userRepository.findUserByUsername(username);
        if (u == null) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        return userMapper.toUserResponse(userRepository.findUserByUsername(username));
    }

    public UserResponse findUserByEmail(String email) {
        User u = userRepository.findUserByEmail(email);
        if (u == null) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        return userMapper.toUserResponse(userRepository.findUserByEmail(email));
    }

    public UserResponse deactivateUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        user.setIsActive(false);
        return userMapper.toUserResponse(userRepository.save(user));
    }

    public void resetPassword(ResetPasswordRequest request) {
        String email = request.getEmail();
        String randomNumber = otpService.generateAndSaveOtp(email);

        String subject = "Yêu cầu reset lại Password của bạn đã thành công!";
        String content = String.format("Mã OTP của bạn là %s. Vui lòng không tiết lộ mã này cho người khác",
                randomNumber);
        mailService.sendSimpleMessage(email, subject, content);
    }

    public String optVerifiedRequest(OtpVerifiedRequest request) {
        return otpService.verifyOtp(request.getEmail(), request.getOtp());
    }

    public UserResponse changePassword(ChangePasswordRequest request) {
        User u = userRepository.findUserByEmail(request.getEmail());
        if (u == null) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            u.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        return userMapper.toUserResponse(userRepository.save(u));
    }

    public void updatePassword(String username, UpdatePasswordRequest request) {
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.OLD_PASSWORD_INCORRECT);
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public User createGoogleUser(String email, String firstName, String lastName, String avatar) {
        User existingUser = userRepository.findUserByEmail(email);
        if (existingUser != null) {
            return existingUser;
        }

        User newUser = User.builder()
                .username(email)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .avatar(avatar)
                .isActive(true)
                .build();

        roleRepository.findById("USER").ifPresent(role -> newUser.setRoles(new HashSet<>(Set.of(role))));
        return userRepository.save(newUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Username không tồn tại: " + username);
        }
        Set<GrantedAuthority> authorities = new HashSet<>();
        if (user.getRoles() != null) {
            user.getRoles().forEach(role -> {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
                if (role.getPermissions() != null) {
                    role.getPermissions().forEach(permission -> {
                        authorities.add(new SimpleGrantedAuthority(permission.getName()));
                    });
                }
            });
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities);
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Long countUser() {
        return userRepository.countActiveUsers();
    }

    public List<UserResponse> getUsersByUsernames(List<String> usernames) {
        return userRepository.findByUsernameIn(usernames).stream()
                .map(userMapper::toUserResponse)
                .toList();
    }
}
