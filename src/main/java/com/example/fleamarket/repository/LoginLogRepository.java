package com.example.fleamarket.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.fleamarket.entity.LoginLog;

public interface LoginLogRepository extends JpaRepository<LoginLog, Long> {
	// 集計用：アクティブユーザー（同じユーザーが1日に何度もログインしても1人と数える）
	@Query("SELECT CAST(l.loginAt AS date) as day, COUNT(DISTINCT l.user.id) " +
			"FROM LoginLog l " +
			"WHERE l.loginAt BETWEEN :start AND :end " +
			"GROUP BY CAST(l.loginAt AS date) " +
			"ORDER BY day ASC")
	List<Object[]> countDailyActiveUsers(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}