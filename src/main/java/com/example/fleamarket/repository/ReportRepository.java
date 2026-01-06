package com.example.fleamarket.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.fleamarket.entity.Report;
import com.example.fleamarket.entity.User;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
	// 【USER用】自分が投稿したものを新しい順に取得
	List<Report> findByReporterOrderByCreatedAtDesc(User reporter);

	// 【ADMIN用】全ての報告を新しい順に取得
	@Query("""
			    SELECT r FROM Report r
			    JOIN FETCH r.reporter
			    ORDER BY r.createdAt DESC
			""")
	List<Report> findAllWithReporterOrderByCreatedAtDesc();
}