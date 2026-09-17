package com.tqt.englishApp.repository;

import com.tqt.englishApp.entity.UserAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAchievementRepository extends JpaRepository<UserAchievement, String> {
    List<UserAchievement> findByUserId(String userId);
    Optional<UserAchievement> findByUserIdAndAchievementCode(String userId, String achievementCode);
}
