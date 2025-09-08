package com.tqt.englishApp.controller;

import com.tqt.englishApp.dto.request.ApiResponse;
import com.tqt.englishApp.dto.request.AuthenticationRequest;
import com.tqt.englishApp.dto.request.GoogleAuthRequest;
import com.tqt.englishApp.dto.request.UserUpdateRequest;
import com.tqt.englishApp.dto.response.AuthenticationResponse;
import com.tqt.englishApp.dto.response.UserResponse;
import com.tqt.englishApp.service.AuthenticateService;
import com.tqt.englishApp.service.UserService;
import com.tqt.englishApp.utils.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiAuthController {
    @Autowired
    AuthenticateService  authenticateService;
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ApiResponse<AuthenticationResponse> login(@RequestBody AuthenticationRequest authenticationRequest) {
        ApiResponse<AuthenticationResponse> response = new ApiResponse<>();
        boolean result = authenticateService.authentication(authenticationRequest);

        if (result){
            try {
                UserResponse user = userService.findUserByUsername(authenticationRequest.getUsername());
                if (user.getIsActive() == true) {
                    String token = JwtUtils.generateToken(
                            user.getUsername(),
                            user.getRole().name()
                    );

                    AuthenticationResponse authResponse = new AuthenticationResponse();
                    authResponse.setAuthenticated(result);
                    authResponse.setToken(token);
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

    @GetMapping("/secure/profile")
    public ResponseEntity<?> getProfile(Principal principal) {
        String username = principal.getName();
        System.out.println("username: " + username);
        return ResponseEntity.ok(userService.findUserByUsername(username));
    }

    @PutMapping(path= "/secure/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProfile(Principal principal, @ModelAttribute @Valid UserUpdateRequest request) {
        String username = principal.getName();
        UserResponse user = userService.findUserByUsername(username);
        return ResponseEntity.ok(userService.updateUser(user.getId(), request));
    }

    @PostMapping("/auth/google-signin")
    public ApiResponse<AuthenticationResponse> googleSignIn(@RequestBody @Valid GoogleAuthRequest request) {
        ApiResponse<AuthenticationResponse> response = new ApiResponse<>();
        boolean result = authenticateService.googleAuth(request);
        if (result) {
            try{
                UserResponse user = userService.findUserByEmail(request.getEmail());
                if (user.getIsActive() == true) {
                    String token = JwtUtils.generateToken(
                            user.getEmail(),
                            user.getRole().name()
                    );

                    AuthenticationResponse authResponse = new AuthenticationResponse();
                    authResponse.setAuthenticated(result);
                    authResponse.setToken(token);
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
            }catch (Exception e){
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
}
