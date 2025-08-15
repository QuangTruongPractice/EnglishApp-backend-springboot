package com.tqt.englishApp.repository;

import com.tqt.englishApp.entity.SubTopic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubTopicRepository extends JpaRepository<SubTopic, Integer> {
    Page<SubTopic> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
