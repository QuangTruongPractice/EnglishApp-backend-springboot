package com.tqt.englishApp.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.tqt.englishApp.dto.request.*;
import com.tqt.englishApp.dto.response.UserResponse;
import com.tqt.englishApp.entity.Role;
import com.tqt.englishApp.entity.User;
import com.tqt.englishApp.exception.AppException;
import com.tqt.englishApp.exception.ErrorCode;
import com.tqt.englishApp.mapper.UserMapper;
import com.tqt.englishApp.repository.RoleRepository;
import com.tqt.englishApp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    UserService userService;
    @Mock
    UserRepository userRepository;
    @Mock
    RoleRepository roleRepository;
    @Mock
    UserMapper userMapper;
    @Mock
    BCryptPasswordEncoder passwordEncoder;
    @Mock
    Cloudinary cloudinary;
    @Mock
    Uploader uploader;
    @Mock
    MultipartFile avatar;
    @Mock
    OtpService otpService;
    @Mock
    MailService mailService;

    User user;
    UserResponse userResponse;

    @BeforeEach
    void init() {
        user = new User();
        user.setId("user-id");
        user.setUsername("testuser");
        user.setEmail("test@gmail.com");
        user.setPassword("password");
        user.setRoles(new HashSet<>(Set.of(Role.builder().name("USER").build())));

        userResponse = new UserResponse();
        userResponse.setId("user-id");
        userResponse.setUsername("testuser");
    }

    @Test
    void createUser_Success() throws IOException {
        UserCreationRequest req = new UserCreationRequest("testuser", "password", "test@gmail.com", "First", "Last",
                null, avatar, true, Set.of("USER"));
        when(avatar.isEmpty()).thenReturn(false);
        when(userRepository.existsUserByUsername(any())).thenReturn(false);
        when(userRepository.existsUserByEmail(any())).thenReturn(false);
        when(userMapper.toUser(any())).thenReturn(user);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(roleRepository.findAllById(any())).thenReturn(List.of(Role.builder().name("USER").build()));
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(), any())).thenReturn(Map.of("secure_url",
                "https://res.cloudinary.com/dabb0yavq/image/upload/v1769493909/iy9ouwlq88ts65u4tfol.jpg"));
        when(userRepository.save(any())).thenReturn(user);
        when(userMapper.toUserResponse(any())).thenReturn(userResponse);

        UserResponse result = userService.createUser(req);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository).save(any());
    }

    @Test
    void createUser_Fail_AvatarRequired() {
        UserCreationRequest req = new UserCreationRequest("testuser", "password", "test@gmail.com", "First", "Last",
                null, null, true, null);
        AppException ex = assertThrows(AppException.class, () -> userService.createUser(req));
        assertEquals(ErrorCode.AVATAR_REQUIRED, ex.getErrorCode());
    }

    @Test
    void createUser_Fail_AvatarEmpty() {
        UserCreationRequest req = new UserCreationRequest("testuser", "password", "test@gmail.com", "First", "Last",
                null, avatar, true, null);
        when(avatar.isEmpty()).thenReturn(true);
        AppException ex = assertThrows(AppException.class, () -> userService.createUser(req));
        assertEquals(ErrorCode.AVATAR_REQUIRED, ex.getErrorCode());
    }

    @Test
    void createUser_Fail_UsernameExisted() {
        UserCreationRequest req = new UserCreationRequest("testuser", "password", "test@gmail.com", "First", "Last",
                null, avatar, true, null);
        when(avatar.isEmpty()).thenReturn(false);
        when(userRepository.existsUserByUsername("testuser")).thenReturn(true);

        AppException ex = assertThrows(AppException.class, () -> userService.createUser(req));
        assertEquals(ErrorCode.USERNAME_EXISTED, ex.getErrorCode());
    }

    @Test
    void createUser_Fail_EmailExisted() {
        UserCreationRequest req = new UserCreationRequest("testuser", "password", "test@gmail.com", "First", "Last",
                null, avatar, true, null);
        when(avatar.isEmpty()).thenReturn(false);
        when(userRepository.existsUserByUsername(any())).thenReturn(false);
        when(userRepository.existsUserByEmail(any())).thenReturn(true);

        AppException ex = assertThrows(AppException.class, () -> userService.createUser(req));
        assertEquals(ErrorCode.EMAIL_EXISTED, ex.getErrorCode());
    }

    @Test
    void createUser_Fail_CloudinaryUploadException() throws IOException {
        UserCreationRequest req = new UserCreationRequest("testuser", "password", "test@gmail.com", "First", "Last",
                null, avatar, true, null);
        when(avatar.isEmpty()).thenReturn(false);
        when(userRepository.existsUserByUsername(any())).thenReturn(false);
        when(userRepository.existsUserByEmail(any())).thenReturn(false);
        when(userMapper.toUser(any())).thenReturn(user);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(), any())).thenThrow(new IOException("Upload failed"));

        AppException ex = assertThrows(AppException.class, () -> userService.createUser(req));
        assertEquals(ErrorCode.UNCATEGORIZED_EXCEPTION, ex.getErrorCode());
    }

    @Test
    void updateUser_Success() throws IOException {
        UserUpdateRequest req = new UserUpdateRequest("newPassword", "NewFirst", "NewLast", null, avatar, true,
                Set.of("USER"));
        when(userRepository.findById("user-id")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(any())).thenReturn("newEncodedPassword");
        when(roleRepository.findAllById(any())).thenReturn(List.of(Role.builder().name("USER").build()));
        when(avatar.isEmpty()).thenReturn(false);
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(), any())).thenReturn(Map.of("secure_url",
                "https://res.cloudinary.com/dabb0yavq/image/upload/v1769493909/iy9ouwlq88ts65u4tfol.jpg"));
        when(userRepository.save(any())).thenReturn(user);
        when(userMapper.toUserResponse(any())).thenReturn(userResponse);

        UserResponse result = userService.updateUser("user-id", req);

        assertNotNull(result);
        verify(userMapper).updateUser(eq(user), any());
        verify(passwordEncoder).encode("newPassword"); // Verify password encoded
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_Success_NoPasswordUpdate() throws IOException {
        UserUpdateRequest req = new UserUpdateRequest("   ", "NewFirst", "NewLast", null, null, true, null); // Empty
                                                                                                             // password
        when(userRepository.findById("user-id")).thenReturn(Optional.of(user));
        // Avatar is null, so no upload logic
        when(userRepository.save(any())).thenReturn(user);
        when(userMapper.toUserResponse(any())).thenReturn(userResponse);

        UserResponse result = userService.updateUser("user-id", req);

        assertNotNull(result);
        verify(passwordEncoder, never()).encode(any()); // Verify password NOT encoded
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_Fail_CloudinaryUploadException() throws IOException {
        UserUpdateRequest req = new UserUpdateRequest(null, "NewFirst", "NewLast", null, avatar, true, null);
        when(userRepository.findById("user-id")).thenReturn(Optional.of(user));
        when(avatar.isEmpty()).thenReturn(false);
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(), any())).thenThrow(new IOException("Upload failed"));

        AppException ex = assertThrows(AppException.class, () -> userService.updateUser("user-id", req));
        assertEquals(ErrorCode.UNCATEGORIZED_EXCEPTION, ex.getErrorCode());
    }

    @Test
    void updateUser_Fail_UserNotFound() {
        UserUpdateRequest req = new UserUpdateRequest("pass", "F", "L", null, avatar, true, null);
        when(userRepository.findById("non-existent")).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> userService.updateUser("non-existent", req));
        assertEquals(ErrorCode.USER_NOT_EXISTED, ex.getErrorCode());
    }

    @Test
    void getUsers_WithKeyword() {
        Map<String, String> params = new HashMap<>();
        params.put("keyword", "test");
        Page<User> userPage = new PageImpl<>(List.of(user));
        when(userRepository.searchByEmailUsernameOrFullName(eq("test"), any())).thenReturn(userPage);
        when(userMapper.toUserResponse(any())).thenReturn(userResponse);

        Page<UserResponse> result = userService.getUsers(params);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(userRepository).searchByEmailUsernameOrFullName(eq("test"), any());
    }

    @Test
    void getUsers_WithoutKeyword() {
        Map<String, String> params = new HashMap<>();
        Page<User> userPage = new PageImpl<>(List.of(user));
        when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);
        when(userMapper.toUserResponse(any())).thenReturn(userResponse);

        Page<UserResponse> result = userService.getUsers(params);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(userRepository).findAll(any(Pageable.class));
    }

    @Test
    void getUserById_Success() {
        when(userRepository.findById("user-id")).thenReturn(Optional.of(user));
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.getUserById("user-id");

        assertNotNull(result);
        assertEquals("user-id", result.getId());
    }

    @Test
    void getUserById_Fail_NotFound() {
        when(userRepository.findById("unknown")).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> userService.getUserById("unknown"));
        assertEquals(ErrorCode.USER_NOT_EXISTED, ex.getErrorCode());
    }

    @Test
    void findUserByUsername_Success() {
        when(userRepository.findUserByUsername("testuser")).thenReturn(user);
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.findUserByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void findUserByUsername_Fail_NotFound() {
        when(userRepository.findUserByUsername("unknown")).thenReturn(null);

        AppException ex = assertThrows(AppException.class, () -> userService.findUserByUsername("unknown"));
        assertEquals(ErrorCode.USER_NOT_EXISTED, ex.getErrorCode());
    }

    @Test
    void findUserByEmail_Success() {
        when(userRepository.findUserByEmail("test@gmail.com")).thenReturn(user);
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.findUserByEmail("test@gmail.com");

        assertNotNull(result);
    }

    @Test
    void findUserByEmail_Fail_NotFound() {
        when(userRepository.findUserByEmail("unknown@gmail.com")).thenReturn(null);

        AppException ex = assertThrows(AppException.class, () -> userService.findUserByEmail("unknown@gmail.com"));
        assertEquals(ErrorCode.USER_NOT_EXISTED, ex.getErrorCode());
    }

    @Test
    void deactivateUser_Success() {
        when(userRepository.findById("user-id")).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);
        when(userMapper.toUserResponse(any())).thenReturn(userResponse);

        UserResponse result = userService.deactivateUser("user-id");

        assertNotNull(result);
        assertFalse(user.getIsActive());
    }

    @Test
    void deactivateUser_Fail_NotFound() {
        when(userRepository.findById("unknown")).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> userService.deactivateUser("unknown"));
        assertEquals(ErrorCode.USER_NOT_EXISTED, ex.getErrorCode());
    }

    @Test
    void resetPassword_Success() {
        ResetPasswordRequest req = new ResetPasswordRequest("test@gmail.com");
        when(otpService.generateAndSaveOtp("test@gmail.com")).thenReturn("1234");

        userService.resetPassword(req);

        verify(otpService).generateAndSaveOtp("test@gmail.com");
        verify(mailService).sendSimpleMessage(eq("test@gmail.com"), anyString(), anyString());
    }

    @Test
    void optVerifiedRequest_Success() {
        OtpVerifiedRequest req = new OtpVerifiedRequest("test@gmail.com", "1234");
        when(otpService.verifyOtp("test@gmail.com", "1234")).thenReturn("Success");

        String result = userService.optVerifiedRequest(req);

        assertEquals("Success", result);
    }

    @Test
    void changePassword_Success() {
        ChangePasswordRequest req = new ChangePasswordRequest("test@gmail.com", "newPass");
        when(userRepository.findUserByEmail("test@gmail.com")).thenReturn(user);
        when(passwordEncoder.encode("newPass")).thenReturn("encodedNewPass");
        when(userRepository.save(any())).thenReturn(user);
        when(userMapper.toUserResponse(any())).thenReturn(userResponse);

        UserResponse result = userService.changePassword(req);

        assertNotNull(result);
        assertEquals("encodedNewPass", user.getPassword());
    }

    @Test
    void changePassword_Fail_NotFound() {
        ChangePasswordRequest req = new ChangePasswordRequest("unknown@gmail.com", "newPass");
        when(userRepository.findUserByEmail("unknown@gmail.com")).thenReturn(null);

        AppException ex = assertThrows(AppException.class, () -> userService.changePassword(req));
        assertEquals(ErrorCode.USER_NOT_EXISTED, ex.getErrorCode());
    }

    @Test
    void createGoogleUser_UserExists() {
        when(userRepository.findUserByEmail("test@gmail.com")).thenReturn(user);

        User result = userService.createGoogleUser("test@gmail.com", "F", "L", "pic");

        assertEquals(user, result);
        verify(userRepository, never()).save(any());
    }

    @Test
    void createGoogleUser_UserNew() {
        when(userRepository.findUserByEmail("test@gmail.com")).thenReturn(null);
        when(userRepository.save(any())).thenReturn(user);
        when(roleRepository.findById("USER")).thenReturn(Optional.of(Role.builder().name("USER").build()));

        User result = userService.createGoogleUser("test@gmail.com", "F", "L", "pic");

        assertNotNull(result);
        verify(userRepository).save(any());
    }

    @Test
    void countUser_Success() {
        when(userRepository.countActiveUsers()).thenReturn(10L);
        Long count = userService.countUser();
        assertEquals(10L, count);
        verify(userRepository).countActiveUsers();
    }

    @Test
    void loadUserByUsername_Success() {
        when(userRepository.findUserByUsername("testuser")).thenReturn(user);

        UserDetails userDetails = userService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void loadUserByUsername_Fail_NotFound() {
        when(userRepository.findUserByUsername("unknown")).thenReturn(null);

        UsernameNotFoundException ex = assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername("unknown"));
        assertTrue(ex.getMessage().contains("Username không tồn tại"));
    }

}
