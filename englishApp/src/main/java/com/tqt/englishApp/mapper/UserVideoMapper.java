package com.tqt.englishApp.mapper;

import com.tqt.englishApp.dto.response.UserVideoResponse;
import com.tqt.englishApp.entity.UserVideoProgress;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserVideoMapper {
    List<UserVideoResponse> toUserVideoResponse(List<UserVideoProgress> userVideoProgress);
}
