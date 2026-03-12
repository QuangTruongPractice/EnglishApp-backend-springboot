package com.tqt.englishApp.controller;

import com.tqt.englishApp.dto.request.UserCreationRequest;
import com.tqt.englishApp.dto.request.UserUpdateRequest;
import com.tqt.englishApp.dto.response.UserResponse;
import com.tqt.englishApp.mapper.UserMapper;
import com.tqt.englishApp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin/users")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @GetMapping
    public String listUsers(Model model, @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "page", required = false, defaultValue = "1") int page) {
        Map<String, String> params = new HashMap<>();
        if (keyword != null && !keyword.isEmpty()) {
            params.put("keyword", keyword);
        }
        params.put("page", String.valueOf(page));

        Page<?> users = userService.getUsers(params);
        model.addAttribute("users", users.getContent());
        model.addAttribute("totalPages", users.getTotalPages());
        model.addAttribute("currentPage", page);
        return "admin/users";
    }

    @GetMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String usersForm(Model model) {
        model.addAttribute("user", new UserCreationRequest());
        model.addAttribute("allRoles", userService.getAllRoles());
        return "admin/users_add_form";
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String addUser(@ModelAttribute("user") @Valid UserCreationRequest request, BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("allRoles", userService.getAllRoles());
            return "admin/users_add_form";
        }
        userService.createUser(request);
        return "redirect:/admin/users";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editUserForm(@PathVariable("id") String userId, Model model) {
        UserResponse userResponse = userService.getUserById(userId);
        model.addAttribute("user", userMapper.toUserUpdateRequest(userResponse));
        model.addAttribute("userId", userId);
        model.addAttribute("currentAvatar", userResponse.getAvatar());
        model.addAttribute("allRoles", userService.getAllRoles());
        return "admin/users_edit_form";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateUser(@PathVariable("id") String userId,
            @ModelAttribute("user") @Valid UserUpdateRequest request, BindingResult result, Model model) {
        if (result.hasErrors()) {
            UserResponse userResponse = userService.getUserById(userId);
            model.addAttribute("allRoles", userService.getAllRoles());
            model.addAttribute("userId", userId);
            model.addAttribute("currentAvatar", userResponse.getAvatar());
            return "admin/users_edit_form";
        }
        userService.updateUser(userId, request);
        return "redirect:/admin/users";
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteUser(@PathVariable("id") String userId) {
        userService.deactivateUser(userId);
        return "redirect:/admin/users";
    }
}
