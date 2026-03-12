package com.tqt.englishApp.mapper;

import com.tqt.englishApp.dto.response.RoleResponse;
import com.tqt.englishApp.entity.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { PermissionMapper.class })
public interface RoleMapper {
    RoleResponse toRoleResponse(Role role);
}
