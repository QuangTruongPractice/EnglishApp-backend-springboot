package com.tqt.englishApp.repository;

import com.tqt.englishApp.entity.AvatarFrame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AvatarFrameRepository extends JpaRepository<AvatarFrame, String> {
    Optional<AvatarFrame> findByFrameKey(String frameKey);
    boolean existsByFrameKey(String frameKey);
}
