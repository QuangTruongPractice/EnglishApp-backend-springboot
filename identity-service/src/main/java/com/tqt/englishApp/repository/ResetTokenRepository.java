package com.tqt.englishApp.repository;

import com.tqt.englishApp.entity.ResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResetTokenRepository extends JpaRepository<ResetToken, Integer> {
    ResetToken findByToken(String token);
}
