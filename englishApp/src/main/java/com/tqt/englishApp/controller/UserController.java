package com.tqt.englishApp.controller;

import com.tqt.englishApp.dto.request.SubTopicRequest;
import com.tqt.englishApp.dto.request.UserCreationRequest;
import com.tqt.englishApp.dto.request.UserUpdateRequest;
import com.tqt.englishApp.dto.response.UserResponse;
import com.tqt.englishApp.service.UserService;
import jakarta.validation.Valid;
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

    @GetMapping
    public String listUsers(Model model, @RequestParam(name = "keyword",required = false) String keyword,
                            @RequestParam(name = "page", required = false, defaultValue = "1") int page) {
        Map<String, String> params = new HashMap<>();
        if (keyword != null && !keyword.isEmpty()) {
            params.put("keyword", keyword);
        }
        params.put("page", String.valueOf(page - 1));

        Page<?> users = userService.getUsers(params);
        model.addAttribute("users", users.getContent());
        model.addAttribute("totalPages", users.getTotalPages());
        model.addAttribute("currentPage", page);
        return "admin/users";
    }

    @GetMapping("/add")
    public String usersForm(Model model) {
        model.addAttribute("user", new UserCreationRequest());
        return "admin/users_add_form";
    }

    @PostMapping("/add")
    public String addUser(@ModelAttribute("user") @Valid UserCreationRequest request, BindingResult result) {
        if (result.hasErrors()) {
            return "admin/users_add_form";
        }
        userService.createUser(request);
        return "redirect:/admin/users";
    }

    @GetMapping("/edit/{id}")
    public String editUserForm(@PathVariable("id") String userId, Model model) {
        UserResponse userResponse = userService.getUserById(userId);

        UserUpdateRequest userUpdateRequest = UserUpdateRequest.builder()
                .firstName(userResponse.getFirstName())
                .lastName(userResponse.getLastName())
                .dob(userResponse.getDob())
                .isActive(userResponse.getIsActive())
                .build();
        model.addAttribute("user", userUpdateRequest);
        model.addAttribute("userId", userId);
        return "admin/users_edit_form";
    }

    @PostMapping("/edit/{id}")
    public String updateUser(@PathVariable("id") String userId, @ModelAttribute("user") @Valid UserUpdateRequest request,
                             BindingResult result) {
        if (result.hasErrors()) {
            return "admin/users_edit_form";
        }
        userService.updateUser(userId, request);
        return "redirect:/admin/users";
    }
}
