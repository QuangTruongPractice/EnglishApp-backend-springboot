package com.tqt.englishApp.repository;

import com.tqt.englishApp.entity.UserVideoProgress;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserVideoProgressRepository extends JpaRepository<UserVideoProgress, Integer> {
    Optional<UserVideoProgress> findByUserIdAndVideoId(String userId, Integer videoId);

    List<UserVideoProgress> findByUserIdAndVideoIdIn(String userId, List<Integer> videoIds);

    Page<UserVideoProgress> findByUserId(String userId, Pageable pageable);

    long countByUserId(String userId);
}
