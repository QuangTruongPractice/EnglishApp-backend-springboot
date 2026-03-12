package com.tqt.englishApp.repository;

import com.tqt.englishApp.entity.MainTopic;
import com.tqt.englishApp.enums.LearningGoal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MainTopicRepository extends JpaRepository<MainTopic, Integer> {
    Page<MainTopic> findByNameContainingIgnoreCase(String name, Pageable pageable);

    List<MainTopic> findByGoalOrderByTopicOrderAsc(LearningGoal goal);

    @Query("SELECT COUNT(st) FROM SubTopic st WHERE st.mainTopic.id = :mainTopicId")
    Long countSubTopicsByMainTopicId(@Param("mainTopicId") Integer mainTopicId);

    @Modifying
    @Query(value = "DELETE vs FROM vocabulary_subtopic vs " +
            "INNER JOIN sub_topic st ON vs.sub_topic_id = st.id " +
            "WHERE st.main_topic_id = :mainTopicId", nativeQuery = true)
    void deleteVocabularySubTopicRelationsByMainTopic(@Param("mainTopicId") Integer mainTopicId);
}
