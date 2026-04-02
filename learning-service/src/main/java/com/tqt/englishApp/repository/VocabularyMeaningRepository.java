package com.tqt.englishApp.repository;

import com.tqt.englishApp.entity.VocabularyMeaning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VocabularyMeaningRepository extends JpaRepository<VocabularyMeaning, Integer> {
    @Query(value = "SELECT * FROM vocabulary_meaning WHERE id != :currentId ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<VocabularyMeaning> findRandomDistractors(@Param("currentId") Integer currentId, @Param("limit") int limit);

    @Query("SELECT vm FROM VocabularyMeaning vm " +
           "JOIN vm.vocabulary v " +
           "JOIN v.subTopics st " +
           "JOIN st.mainTopic mt " +
           "WHERE mt.goal = :goal " +
           "AND v.level IN :levels " +
           "AND NOT EXISTS (SELECT 1 FROM UserVocabularyProgress uvp WHERE uvp.userId = :userId AND uvp.meaning = vm) " +
           "ORDER BY mt.topicOrder ASC, st.topicOrder ASC")
    List<VocabularyMeaning> findNewMeanings(@Param("userId") String userId, 
                                            @Param("goal") com.tqt.englishApp.enums.LearningGoal goal, 
                                            @Param("levels") List<com.tqt.englishApp.enums.Level> levels);

    @Query("SELECT vm FROM VocabularyMeaning vm " +
           "JOIN vm.vocabulary v " +
           "JOIN v.subTopics st " +
           "WHERE st.id = :subTopicId AND vm.type = :type AND vm.id != :excludedId")
    List<VocabularyMeaning> findDistractorsBySubTopicAndType(@Param("subTopicId") Integer subTopicId, 
                                                              @Param("type") com.tqt.englishApp.enums.Type type, 
                                                              @Param("excludedId") Integer excludedId);

    @Query("SELECT vm FROM VocabularyMeaning vm " +
           "JOIN vm.vocabulary v " +
           "JOIN v.subTopics st " +
           "JOIN st.mainTopic mt " +
           "WHERE mt.id = :mainTopicId AND vm.type = :type AND vm.id != :excludedId")
    List<VocabularyMeaning> findDistractorsByMainTopicAndType(@Param("mainTopicId") Integer mainTopicId, 
                                                               @Param("type") com.tqt.englishApp.enums.Type type, 
                                                               @Param("excludedId") Integer excludedId);
}
