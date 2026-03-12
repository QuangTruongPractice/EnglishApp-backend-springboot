package com.tqt.englishApp.mapper;

import com.tqt.englishApp.dto.response.PermissionResponse;
import com.tqt.englishApp.entity.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    PermissionResponse toPermissionResponse(Permission permission);
}
