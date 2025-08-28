package com.tqt.englishApp.repository;

import com.tqt.englishApp.entity.Subtitles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubtitlesRepository extends JpaRepository<Subtitles,Integer> {
    List<Subtitles> findByVideoId(Integer videoId);
}
