package com.tqt.englishApp.controller;

import com.tqt.englishApp.dto.request.ApiResponse;
import com.tqt.englishApp.dto.request.UserCreationRequest;
import com.tqt.englishApp.dto.request.UserUpdateRequest;
import com.tqt.englishApp.dto.response.UserResponse;
import com.tqt.englishApp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiUserController {
    @Autowired
    private UserService userService;

    @PostMapping(path = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<UserResponse> createUser(@ModelAttribute @Valid UserCreationRequest user) {
        ApiResponse<UserResponse> response = new ApiResponse<>();
        response.setResult(userService.createUser(user));
        return response;
    }

    @GetMapping("/users")
    public ApiResponse<Page<UserResponse>> getUsers(@RequestParam Map<String, String> params) {
        ApiResponse<Page<UserResponse>> response = new ApiResponse<>();
        response.setResult(userService.getUsers(params));
        return response;
    }

    @GetMapping("/users/{userId}")
    public ApiResponse<UserResponse> getUser(@PathVariable("userId") String userId) {
        ApiResponse<UserResponse> response = new ApiResponse<>();
        response.setResult(userService.getUserById(userId));
        return response;
    }

    @PutMapping(path="/users/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<UserResponse> updateUser(@ModelAttribute UserUpdateRequest request, @PathVariable("userId") String userId) {
        ApiResponse<UserResponse> response = new ApiResponse<>();
        response.setResult(userService.updateUser(userId, request));
        return response;
    }

    @DeleteMapping("/users/{userId}")
    public ApiResponse<UserResponse> deleteUser(@PathVariable String userId) {
        ApiResponse<UserResponse> response = new ApiResponse<>();
        response.setResult(userService.deactivateUser(userId));
        return response;
    }

}
