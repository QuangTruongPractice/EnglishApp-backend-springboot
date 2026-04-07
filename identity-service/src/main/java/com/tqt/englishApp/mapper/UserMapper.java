package com.tqt.englishApp.mapper;

import com.tqt.englishApp.dto.request.UserCreationRequest;
import com.tqt.englishApp.dto.request.UserUpdateRequest;
import com.tqt.englishApp.dto.response.UserResponse;
import com.tqt.englishApp.dto.response.RoleResponse;
import com.tqt.englishApp.entity.User;
import com.tqt.englishApp.entity.Role;
import org.mapstruct.*;
import java.util.Set;

@Mapper(componentModel = "spring", uses = { RoleMapper.class })
public interface UserMapper {
    @Mapping(target = "avatar", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "roles", ignore = true)
    User toUser(UserCreationRequest user);

    @Mapping(target = "permissions", expression = "java(mapPermissionsFromRoles(user.getRoles()))")
    UserResponse toUserResponse(User user);

    default Set<String> mapPermissionsFromRoles(Set<Role> roles) {
        if (roles == null)
            return null;
        return roles.stream()
                .filter(role -> role.getPermissions() != null)
                .flatMap(role -> role.getPermissions().stream())
                .map(com.tqt.englishApp.entity.Permission::getName)
                .collect(java.util.stream.Collectors.toSet());
    }

    @Mapping(target = "avatar", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", expression = "java(mapRoles(userResponse.getRoles()))")
    UserUpdateRequest toUserUpdateRequest(UserResponse userResponse);

    default Set<String> mapRoles(Set<RoleResponse> roles) {
        if (roles == null)
            return null;
        return roles.stream()
                .map(RoleResponse::getName)
                .collect(java.util.stream.Collectors.toSet());
    }

    @Mapping(target = "avatar", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
