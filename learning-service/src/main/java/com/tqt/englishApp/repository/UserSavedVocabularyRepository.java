package com.tqt.englishApp.repository;

import com.tqt.englishApp.entity.UserSavedVocabulary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSavedVocabularyRepository extends JpaRepository<UserSavedVocabulary, Integer> {
    Page<UserSavedVocabulary> findByUserId(String userId, Pageable pageable);
    Optional<UserSavedVocabulary> findByUserIdAndVocabularyId(String userId, Integer vocabularyId);
    boolean existsByUserIdAndVocabularyId(String userId, Integer vocabularyId);

    long countByUserId(String userId);
}
