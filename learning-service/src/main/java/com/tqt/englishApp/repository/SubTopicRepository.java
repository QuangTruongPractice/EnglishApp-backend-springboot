package com.tqt.englishApp.repository;

import com.tqt.englishApp.entity.SubTopic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SubTopicRepository extends JpaRepository<SubTopic, Integer> {
    Page<SubTopic> findByNameContainingIgnoreCase(String name, Pageable pageable);
    @Query("SELECT COUNT(v) FROM Vocabulary v JOIN v.subTopics st WHERE st.id = :subTopicId")
    Long countVocabularyBySubTopicId(@Param("subTopicId") Integer subTopicId);

    @Modifying
    @Query(value = "DELETE FROM vocabulary_subtopic WHERE sub_topic_id = :subTopicId",
            nativeQuery = true)
    void deleteVocabularySubTopicRelations(@Param("subTopicId") Integer subTopicId);
}
