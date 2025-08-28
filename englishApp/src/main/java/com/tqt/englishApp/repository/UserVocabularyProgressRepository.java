package com.tqt.englishApp.repository;

import com.tqt.englishApp.entity.UserVocabularyProgress;
import com.tqt.englishApp.enums.VocabularyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserVocabularyProgressRepository extends JpaRepository<UserVocabularyProgress,Integer> {
    Optional<UserVocabularyProgress> findByUserIdAndVocabularyId(String userId, Integer vocabularyId);
    List<UserVocabularyProgress> findByUserId(String userId);
    List<UserVocabularyProgress> findByUserIdAndStatus(String userId, VocabularyStatus status);
}
