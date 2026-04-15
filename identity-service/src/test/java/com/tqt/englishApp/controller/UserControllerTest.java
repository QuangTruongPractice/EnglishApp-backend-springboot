package com.tqt.englishApp.controller;

import com.tqt.englishApp.controller.admin.UserController;
import com.tqt.englishApp.dto.request.UserCreationRequest;
import com.tqt.englishApp.dto.request.UserUpdateRequest;
import com.tqt.englishApp.dto.response.UserResponse;
import com.tqt.englishApp.mapper.UserMapper;
import com.tqt.englishApp.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private UserService userService;

        @MockitoBean
        private UserMapper userMapper;

        @Test
        void listUsers_WithKeyword_Success() throws Exception {
                Page<UserResponse> usersPage = new PageImpl<>(Collections.emptyList());
                when(userService.getUsers(anyMap())).thenReturn(usersPage);

                mockMvc.perform(get("/admin/users")
                                .param("keyword", "test")
                                .param("page", "1"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("admin/users"))
                                .andExpect(model().attributeExists("users"))
                                .andExpect(model().attribute("totalPages", usersPage.getTotalPages()))
                                .andExpect(model().attribute("currentPage", 1));

                verify(userService)
                                .getUsers(argThat(params -> "test".equals(params.get("keyword"))
                                                && "1".equals(params.get("page"))));
        }

        @Test
        void listUsers_WithoutKeyword_Success() throws Exception {
                Page<UserResponse> usersPage = new PageImpl<>(Collections.emptyList());
                when(userService.getUsers(anyMap())).thenReturn(usersPage);

                mockMvc.perform(get("/admin/users"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("admin/users"));

                verify(userService)
                                .getUsers(argThat(params -> !params.containsKey("keyword")
                                                && "1".equals(params.get("page"))));
        }

        @Test
        void usersForm_Success() throws Exception {
                mockMvc.perform(get("/admin/users/add"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("admin/users_add_form"))
                                .andExpect(model().attributeExists("user"));
        }

        @Test
        void addUser_Success() throws Exception {
                mockMvc.perform(post("/admin/users/add")
                                .flashAttr("user", new UserCreationRequest()))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/admin/users"));

                verify(userService).createUser(any(UserCreationRequest.class));
        }

        @Test
        void addUser_ValidationError() throws Exception {
                mockMvc.perform(post("/admin/users/add")
                                .param("username", "")
                                .flashAttr("user", new UserCreationRequest()))
                                .andExpect(status().isOk())
                                .andExpect(view().name("admin/users_add_form"));

                verify(userService, never()).createUser(any());
        }

        @Test
        void editUserForm_Success() throws Exception {
                String userId = "user123";
                UserResponse userResponse = new UserResponse();
                UserUpdateRequest updateRequest = new UserUpdateRequest();

                when(userService.getUserById(userId)).thenReturn(userResponse);
                when(userMapper.toUserUpdateRequest(userResponse)).thenReturn(updateRequest);

                mockMvc.perform(get("/admin/users/edit/{id}", userId))
                                .andExpect(status().isOk())
                                .andExpect(view().name("admin/users_edit_form"))
                                .andExpect(model().attribute("user", updateRequest))
                                .andExpect(model().attribute("userId", userId));
        }

        @Test
        void updateUser_Success() throws Exception {
                String userId = "user123";
                mockMvc.perform(post("/admin/users/edit/{id}", userId)
                                .flashAttr("user", new UserUpdateRequest()))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/admin/users"));

                verify(userService).updateUser(eq(userId), any(UserUpdateRequest.class));
        }

        @Test
        void updateUser_ValidationError() throws Exception {
                String userId = "user123";
                UserResponse userResponse = new UserResponse();
                userResponse.setAvatar("avatar.jpg");
                when(userService.getUserById(userId)).thenReturn(userResponse);

                mockMvc.perform(post("/admin/users/edit/{id}", userId)
                                .param("password", "1")
                                .flashAttr("user", new UserUpdateRequest()))
                                .andExpect(status().isOk())
                                .andExpect(view().name("admin/users_edit_form"))
                                .andExpect(model().attribute("currentAvatar", "avatar.jpg"));

                verify(userService, never()).updateUser(anyString(), any());
        }
}
