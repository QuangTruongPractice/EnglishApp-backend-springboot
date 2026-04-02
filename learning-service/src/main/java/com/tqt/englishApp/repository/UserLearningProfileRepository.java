package com.tqt.englishApp.repository;

import com.tqt.englishApp.entity.UserLearningProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserLearningProfileRepository extends JpaRepository<UserLearningProfile, Integer> {
    Optional<UserLearningProfile> findByUserId(String userId);

    @Query("SELECT ulp FROM UserLearningProfile ulp ORDER BY ulp.weeklyXp DESC LIMIT 10")
    List<UserLearningProfile> findTop10ByWeeklyXp();

    @Query("SELECT ulp FROM UserLearningProfile ulp ORDER BY ulp.totalXp DESC LIMIT 10")
    List<UserLearningProfile> findTop10ByTotalXp();

    @Query("SELECT COUNT(ulp) + 1 FROM UserLearningProfile ulp WHERE ulp.weeklyXp > (SELECT u.weeklyXp FROM UserLearningProfile u WHERE u.userId = :userId)")
    int getWeeklyRank(String userId);
}
