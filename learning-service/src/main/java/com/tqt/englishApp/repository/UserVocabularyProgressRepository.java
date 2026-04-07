package com.tqt.englishApp.repository;

import com.tqt.englishApp.dto.response.LeaderBoardResponse;
import com.tqt.englishApp.entity.UserVocabularyProgress;
import com.tqt.englishApp.enums.VocabularyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserVocabularyProgressRepository extends JpaRepository<UserVocabularyProgress, Integer> {
        Optional<UserVocabularyProgress> findByUserIdAndMeaningId(String userId, Integer meaningId);

        List<UserVocabularyProgress> findByUserId(String userId);

        Page<UserVocabularyProgress> findByUserId(String userId, Pageable pageable);

        List<UserVocabularyProgress> findByUserIdAndMeaning_Vocabulary_IdIn(String userId, List<Integer> vocabularyIds);

        long countByUserIdAndStatus(String userId, VocabularyStatus status);

        @Query("SELECT u FROM UserVocabularyProgress u " +
                        "WHERE u.userId = :userId " +
                        "AND u.nextReviewAt <= :now " +
                        "AND u.status != 'MASTERED'")
        List<UserVocabularyProgress> findDueReviews(String userId, LocalDateTime now);

        @Query("SELECT new com.tqt.englishApp.dto.response.LeaderBoardResponse(" +
                        "u.userId, 'Unknown User', COUNT(u), MIN(u.updatedAt)) " +
                        "FROM UserVocabularyProgress u " +
                        "WHERE u.status = 'MASTERED' " +
                        "GROUP BY u.userId " +
                        "ORDER BY COUNT(u) DESC")
        List<LeaderBoardResponse> getUserRanking();
}
