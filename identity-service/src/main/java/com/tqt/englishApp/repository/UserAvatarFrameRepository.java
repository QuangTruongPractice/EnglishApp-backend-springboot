package com.tqt.englishApp.repository;

import com.tqt.englishApp.entity.UserAvatarFrame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAvatarFrameRepository extends JpaRepository<UserAvatarFrame, String> {
    List<UserAvatarFrame> findByUserId(String userId);
    Optional<UserAvatarFrame> findByUserIdAndFrameKey(String userId, String frameKey);
    boolean existsByUserIdAndFrameKey(String userId, String frameKey);
}
