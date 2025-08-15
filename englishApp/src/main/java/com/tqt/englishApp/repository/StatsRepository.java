package com.tqt.englishApp.repository;

import com.tqt.englishApp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<User, Integer> {
    @Query("SELECT YEAR(u.createdAt) as year, COUNT(u) AS total " +
            "FROM User u " +
            "GROUP BY YEAR(u.createdAt)" +
            "ORDER BY YEAR(u.createdAt) ASC"
    )
    List<Object[]> countUsersEveryYear();
    @Query("SELECT MONTH(u.createdAt), COUNT(u) AS total " +
            "FROM User u " +
            "WHERE YEAR(u.createdAt) = :year " +
            "GROUP BY MONTH(u.createdAt) " +
            "ORDER BY MONTH(u.createdAt)"
    )
    List<Object[]> countUsersByYear(@Param("year") int year);
    @Query("SELECT CEIL(MONTH(u.createdAt) / 3.0), COUNT(u) AS total " +
            "FROM User u " +
            "WHERE YEAR(u.createdAt) = :year AND CEIL(MONTH(u.createdAt) / 3.0) = :quarter " +
            "GROUP BY YEAR(u.createdAt), CEIL(MONTH(u.createdAt) / 3.0) " +
            "ORDER BY CEIL(MONTH(u.createdAt) / 3.0) ASC")
    List<Object[]> countUsersByQuarter(@Param("year") int year, @Param("quarter") int quarter);
    @Query("SELECT DAY(u.createdAt), COUNT(u) AS total " +
            "FROM User u " +
            "WHERE YEAR(u.createdAt) = :year AND MONTH(u.createdAt) = :month " +
            "GROUP BY DAY(u.createdAt) " +
            "ORDER BY DAY(u.createdAt) ASC")
    List<Object[]> countUsersByDayInMonth(@Param("year") int year, @Param("month") int month);
}
