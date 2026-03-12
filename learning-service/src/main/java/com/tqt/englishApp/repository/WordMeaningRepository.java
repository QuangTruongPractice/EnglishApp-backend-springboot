package com.tqt.englishApp.repository;

import com.tqt.englishApp.entity.WordMeaning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface WordMeaningRepository extends JpaRepository<WordMeaning, Integer> {
    @Query(value = "SELECT * FROM word_meaning WHERE id != :currentId ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<WordMeaning> findRandomDistractors(@Param("currentId") Integer currentId, @Param("limit") int limit);
}
