package com.tqt.englishApp.repository;

import com.tqt.englishApp.entity.MainTopic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MainTopicRepository extends JpaRepository<MainTopic, Integer> {
    Page<MainTopic> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
