package com.tqt.englishApp.repository;

import com.tqt.englishApp.entity.Vocabulary;
import com.tqt.englishApp.enums.Level;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VocabularyRepository extends JpaRepository<Vocabulary, Integer> {
        Page<Vocabulary> findByWordContainingIgnoreCase(String word, Pageable pageable);

        List<Vocabulary> findTop5ByOrderByIdDesc();

        long countByLevel(Level level);

        List<Vocabulary> findTop5ByWordContainingIgnoreCase(String word);

        @Query("SELECT v FROM Vocabulary v WHERE v.subTopics IS EMPTY")
        List<Vocabulary> findAllWithoutSubTopics();


        @Query(value = "SELECT * FROM vocabulary WHERE id != :currentId ORDER BY RAND() LIMIT :limit", nativeQuery = true)
        List<Vocabulary> findRandomDistractors(@Param("currentId") Integer currentId, @Param("limit") int limit);

        @Query("SELECT DISTINCT v FROM Vocabulary v " +
                        "JOIN v.subTopics st " +
                        "WHERE v.level = :level " +
                        "AND st.mainTopic.id = :topicId " +
                        "AND NOT EXISTS (" +
                        "  SELECT 1 FROM UserVocabularyProgress p " +
                        "  JOIN p.meaning m " +
                        "  WHERE m.vocabulary = v AND p.userId = :userId" +
                        ")")
        List<Vocabulary> findNewVocabulariesByLevelAndTopic(
                        @Param("userId") String userId,
                        @Param("level") Level level,
                        @Param("topicId") Integer topicId,
                        Pageable pageable);
}
