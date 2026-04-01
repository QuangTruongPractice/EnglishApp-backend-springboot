package com.tqt.englishApp.repository;

import com.tqt.englishApp.entity.SessionQuiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionQuizRepository extends JpaRepository<SessionQuiz, Integer> {
    Optional<SessionQuiz> findBySessionIdAndQuizId(Integer sessionId, Integer quizId);
    List<SessionQuiz> findBySessionId(Integer sessionId);
}
