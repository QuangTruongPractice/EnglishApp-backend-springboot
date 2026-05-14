package com.tqt.englishApp.controller.client;

import com.tqt.englishApp.dto.request.*;
import com.tqt.englishApp.dto.response.AuthenticationResponse;
import com.tqt.englishApp.dto.response.UserResponse;
import com.tqt.englishApp.service.AuthenticateService;
import com.tqt.englishApp.service.UserService;
import com.tqt.englishApp.utils.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiAuthController {
    @Autowired
    AuthenticateService authenticateService;
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ApiResponse<AuthenticationResponse> login(@RequestBody AuthenticationRequest authenticationRequest) {
        ApiResponse<AuthenticationResponse> response = new ApiResponse<>();
        boolean result = authenticateService.authentication(authenticationRequest);

        if (result) {
            try {
                UserResponse user = userService.findUserByUsername(authenticationRequest.getUsername());
                if (user.getIsActive() != null && user.getIsActive()) {
                    String roles = user.getRoles().stream()
                            .map(r -> r.getName())
                            .collect(Collectors.joining(" "));
                    String token = JwtUtils.generateToken(
                            user.getUsername(),
                            roles);
                    String refreshToken = JwtUtils.generateRefreshToken(
                            user.getUsername(),
                            roles);

                    AuthenticationResponse authResponse = new AuthenticationResponse();
                    authResponse.setAuthenticated(result);
                    authResponse.setToken(token);
                    authResponse.setRefreshToken(refreshToken);
                    response.setResult(authResponse);
                    response.setMessage("Đăng nhập thành công");
                    return response;

                } else {
                    AuthenticationResponse authResponse = new AuthenticationResponse();
                    authResponse.setAuthenticated(false);
                    authResponse.setToken(null);
                    response.setResult(authResponse);
                    response.setMessage("Tài khoản chưa được duyệt hoặc đã bị khóa");
                    response.setCode(403);
                    return response;
                }

            } catch (Exception e) {
                e.printStackTrace();
                AuthenticationResponse authResponse = new AuthenticationResponse();
                authResponse.setAuthenticated(false);
                authResponse.setToken(null);
                response.setResult(authResponse);
                response.setMessage("Lỗi khi tạo JWT: " + e.getMessage());
                response.setCode(500);
                return response;
            }
        }
        AuthenticationResponse authResponse = new AuthenticationResponse();
        authResponse.setAuthenticated(false);
        authResponse.setToken(null);
        response.setResult(authResponse);
        response.setMessage("Sai tên đăng nhập hoặc mật khẩu");
        response.setCode(401);
        return response;
    }

    @PostMapping("/auth/google-signin")
    public ApiResponse<AuthenticationResponse> googleSignIn(@RequestBody @Valid GoogleAuthRequest request) {
        ApiResponse<AuthenticationResponse> response = new ApiResponse<>();
        boolean result = authenticateService.googleAuth(request);
        if (result) {
            try {
                UserResponse user = userService.findUserByEmail(request.getEmail());
                if (user.getIsActive() != null && user.getIsActive()) {
                    String roles = user.getRoles().stream()
                            .map(r -> r.getName())
                            .collect(Collectors.joining(" "));
                    String token = JwtUtils.generateToken(
                            user.getEmail(),
                            roles);
                    String refreshToken = JwtUtils.generateRefreshToken(
                            user.getEmail(),
                            roles);

                    AuthenticationResponse authResponse = new AuthenticationResponse();
                    authResponse.setAuthenticated(result);
                    authResponse.setToken(token);
                    authResponse.setRefreshToken(refreshToken);
                    response.setResult(authResponse);
                    response.setMessage("Đăng nhập thành công");
                    return response;

                } else {
                    AuthenticationResponse authResponse = new AuthenticationResponse();
                    authResponse.setAuthenticated(false);
                    authResponse.setToken(null);
                    response.setResult(authResponse);
                    response.setMessage("Tài khoản chưa được duyệt hoặc đã bị khóa");
                    response.setCode(403);
                    return response;
                }
            } catch (Exception e) {
                e.printStackTrace();
                AuthenticationResponse authResponse = new AuthenticationResponse();
                authResponse.setAuthenticated(false);
                authResponse.setToken(null);
                response.setResult(authResponse);
                response.setMessage("Lỗi khi tạo JWT: " + e.getMessage());
                response.setCode(500);
                return response;
            }

        } else {
            response.setMessage("Xác thực Google thất bại");
            return response;
        }
    }

    @GetMapping("/secure/profile")
    public ResponseEntity<?> getProfile(Principal principal) {
        String username = principal.getName();
        System.out.println("username: " + username);
        return ResponseEntity.ok(userService.findUserByUsername(username));
    }

    @PutMapping(path = "/secure/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProfile(Principal principal, @ModelAttribute @Valid UserUpdateRequest request) {
        String username = principal.getName();
        UserResponse user = userService.findUserByUsername(username);
        return ResponseEntity.ok(userService.updateUser(user.getId(), request));
    }

    @PostMapping("/auth/refresh")
    public ApiResponse<AuthenticationResponse> refresh(@RequestBody RefreshRequest request) {
        ApiResponse<AuthenticationResponse> response = new ApiResponse<>();
        try {
            var claims = JwtUtils.validateTokenAndGetClaims(request.getToken());
            if (claims != null) {
                String username = (String) claims.get("username");
                String roles = (String) claims.get("role");

                String newToken = JwtUtils.generateToken(username, roles);
                String newRefreshToken = JwtUtils.generateRefreshToken(username, roles);

                AuthenticationResponse authResponse = new AuthenticationResponse();
                authResponse.setAuthenticated(true);
                authResponse.setToken(newToken);
                authResponse.setRefreshToken(newRefreshToken);

                response.setResult(authResponse);
                response.setMessage("Làm mới token thành công");
                return response;
            }
        } catch (Exception e) {
            response.setMessage("Token không hợp lệ hoặc đã hết hạn: " + e.getMessage());
            response.setCode(401);
            return response;
        }
        response.setMessage("Làm mới token thất bại");
        response.setCode(401);
        return response;
    }
}
