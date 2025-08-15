package com.tqt.englishApp.repository;

import com.tqt.englishApp.entity.WordType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WordTypeRepository extends JpaRepository<WordType,Integer> {
}
