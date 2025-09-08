package com.tqt.englishApp.repository;

import com.tqt.englishApp.dto.response.LeaderBoardResponse;
import com.tqt.englishApp.entity.UserVocabularyProgress;
import com.tqt.englishApp.enums.VocabularyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserVocabularyProgressRepository extends JpaRepository<UserVocabularyProgress,Integer> {
    Optional<UserVocabularyProgress> findByUserIdAndVocabularyId(String userId, Integer vocabularyId);
    List<UserVocabularyProgress> findByUserId(String userId);
    List<UserVocabularyProgress> findByUserIdAndStatus(String userId, VocabularyStatus status);

    @Query("SELECT new com.tqt.englishApp.dto.response.LeaderBoardResponse(" +
            "u.user.id, CONCAT(u.user.firstName, ' ', u.user.lastName), COUNT(u), MIN(u.updatedAt)) " +
            "FROM UserVocabularyProgress u " +
            "WHERE u.status = 'COMPLETED' " +
            "GROUP BY u.user.id, u.user.firstName, u.user.lastName " +
            "ORDER BY COUNT(u) DESC")
    List<LeaderBoardResponse> getUserRanking();
}
