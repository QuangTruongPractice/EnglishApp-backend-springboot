package com.tqt.englishApp.repository;

import com.tqt.englishApp.entity.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer,Integer> {
    Page<Answer> findByAnswerContainingIgnoreCase(String answer, Pageable pageable);
}
