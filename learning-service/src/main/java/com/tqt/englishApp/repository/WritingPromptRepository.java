package com.tqt.englishApp.repository;

import com.tqt.englishApp.entity.WritingPrompt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

@Repository
public interface WritingPromptRepository extends JpaRepository<WritingPrompt, Integer> {
}
