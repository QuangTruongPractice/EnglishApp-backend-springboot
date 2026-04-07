package com.tqt.englishApp.mapper;

import com.tqt.englishApp.dto.response.UserLearningProfileResponse;
import com.tqt.englishApp.entity.UserLearningProfile;
import com.tqt.englishApp.service.UserStatsService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class UserLearningProfileMapper {

    @Autowired
    protected UserStatsService userStatsService;

    @Mapping(target = "currentStreak", expression = "java(userStatsService.calculateCurrentStreak(profile.getUserId()))")
    @Mapping(target = "longestStreak", expression = "java(userStatsService.calculateLongestStreak(profile.getUserId()))")
    public abstract UserLearningProfileResponse toResponse(UserLearningProfile profile);
}
