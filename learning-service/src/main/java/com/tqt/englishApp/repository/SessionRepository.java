package com.tqt.englishApp.repository;

import com.tqt.englishApp.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session, Integer> {
    Optional<Session> findByUserIdAndDate(String userId, LocalDate date);
}
