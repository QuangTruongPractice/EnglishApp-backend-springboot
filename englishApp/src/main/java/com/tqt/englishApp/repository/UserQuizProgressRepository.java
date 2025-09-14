package com.tqt.englishApp.repository;

import com.tqt.englishApp.entity.UserQuizProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserQuizProgressRepository extends JpaRepository<UserQuizProgress,Integer> {
    Optional<UserQuizProgress> findByUserIdAndQuizId(String userId, Integer quizId);
}
