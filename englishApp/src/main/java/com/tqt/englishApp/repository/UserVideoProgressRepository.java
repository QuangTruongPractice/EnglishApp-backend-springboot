package com.tqt.englishApp.repository;

import com.tqt.englishApp.entity.UserVideoProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserVideoProgressRepository extends JpaRepository<UserVideoProgress,Integer> {
    Optional<UserVideoProgress> findByUserIdAndVideoId(String userId, Integer videoId);
    List<UserVideoProgress> findByUserId(String userId);
    List<UserVideoProgress> findByUserIdAndIsCompleted(String userId, Boolean isCompleted);
}
