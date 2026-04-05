package com.tqt.englishApp.repository;

import com.tqt.englishApp.entity.MatchItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchItemRepository extends JpaRepository<MatchItem, Integer> {
    List<MatchItem> findAllByQuizId(Integer quizId);
}
