package com.tqt.englishApp.repository;

import com.tqt.englishApp.entity.Quiz;
import com.tqt.englishApp.enums.QuizType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Integer> {
    Page<Quiz> findByQuestionContainingIgnoreCase(String question, Pageable pageable);

    List<Quiz> findTop5ByOrderByIdDesc();

    long countByType(QuizType type);
}
