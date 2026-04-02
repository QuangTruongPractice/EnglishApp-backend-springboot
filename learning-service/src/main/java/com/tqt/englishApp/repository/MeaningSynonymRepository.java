package com.tqt.englishApp.repository;

import com.tqt.englishApp.entity.MeaningSynonym;
import com.tqt.englishApp.entity.VocabularyMeaning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MeaningSynonymRepository extends JpaRepository<MeaningSynonym, Integer> {
    Optional<MeaningSynonym> findByMeaningAndSynonymMeaning(VocabularyMeaning meaning, VocabularyMeaning synonymMeaning);
    void deleteByMeaningAndSynonymMeaning(VocabularyMeaning meaning, VocabularyMeaning synonymMeaning);
}
