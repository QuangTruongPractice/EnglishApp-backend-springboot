package com.tqt.englishApp.repository;

import com.tqt.englishApp.entity.Video;
import com.tqt.englishApp.entity.Vocabulary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoRepository extends JpaRepository<Video, Integer> {
    Page<Video> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}
