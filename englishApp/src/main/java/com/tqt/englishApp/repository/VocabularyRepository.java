package com.tqt.englishApp.repository;

import com.tqt.englishApp.entity.Vocabulary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VocabularyRepository extends JpaRepository<Vocabulary,Integer> {
    Page<Vocabulary> findByWordContainingIgnoreCase(String word, Pageable pageable);
}
