package com.tqt.englishApp.repository;

import com.tqt.englishApp.entity.User;
import com.tqt.englishApp.entity.Vocabulary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    boolean existsUserByUsername(String username);
    boolean existsUserByEmail(String email);
    User findUserByUsername(String username);
    @Query("""
    SELECT u FROM User u
    WHERE LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
       OR LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%'))
       OR LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :keyword, '%'))
""")
    Page<User> searchByEmailUsernameOrFullName(@Param("keyword") String keyword, Pageable pageable);
}
