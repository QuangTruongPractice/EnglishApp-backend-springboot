package com.tqt.englishApp.repository;

import com.tqt.englishApp.entity.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, String> {
    Optional<Achievement> findByCode(String code);
    boolean existsByCode(String code);
}
