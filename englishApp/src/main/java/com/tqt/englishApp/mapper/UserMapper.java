package com.tqt.englishApp.mapper;

import com.tqt.englishApp.dto.request.UserCreationRequest;
import com.tqt.englishApp.dto.request.UserUpdateRequest;
import com.tqt.englishApp.dto.response.UserResponse;
import com.tqt.englishApp.entity.User;
import org.mapstruct.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "avatar", ignore = true)
    @Mapping(target = "password", ignore = true)
    User toUser(UserCreationRequest user);

    UserResponse toUserResponse(User user);

    @Mapping(target = "avatar", ignore = true)
    @Mapping(target = "password", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);

    default String mapMultipartFile(MultipartFile file) {
        return null;
    }
}
