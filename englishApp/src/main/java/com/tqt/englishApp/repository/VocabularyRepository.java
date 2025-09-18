package com.tqt.englishApp.repository;

import com.tqt.englishApp.entity.Vocabulary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VocabularyRepository extends JpaRepository<Vocabulary,Integer> {
    Page<Vocabulary> findByWordContainingIgnoreCase(String word, Pageable pageable);
    @Query("SELECT v FROM Vocabulary v WHERE v.subTopics IS EMPTY")
    List<Vocabulary> findAllWithoutSubTopics();
}
