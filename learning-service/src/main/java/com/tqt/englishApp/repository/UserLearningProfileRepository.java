package com.tqt.englishApp.repository;

import com.tqt.englishApp.entity.UserLearningProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserLearningProfileRepository extends JpaRepository<UserLearningProfile, Integer> {
    Optional<UserLearningProfile> findByUserId(String userId);
}
