package com.example.fleamarket.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.fleamarket.entity.Report;
import com.example.fleamarket.entity.User;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
	// 【USER用】自分が投稿したものを新しい順に取得
	List<Report> findByReporterOrderByCreatedAtDesc(User reporter);

	// 自分が報告したレポートの中で、特定のステータスのものを取得
	List<Report> findByReporterAndStatus(User reporter, String status);

	// 【ADMIN用】全ての報告を新しい順に取得
	@Query("""
			    SELECT r FROM Report r
			    JOIN FETCH r.reporter
			    ORDER BY r.createdAt DESC
			""")
	List<Report> findAllWithReporterOrderByCreatedAtDesc();

	// キーワード(message) ＋ タイプ ＋ ステータス で検索
	@Query("SELECT r FROM Report r WHERE " +
			"(lower(r.message) LIKE lower(concat('%', :q, '%'))) " +
			"AND (:hasType = false OR r.reportType = :type) " +
			"AND (:hasStatus = false OR r.status = :status) " +
			"AND (:hasReporterId = false OR r.reporter.id = :reporterId) " +
			"ORDER BY r.id DESC")
	List<Report> searchReports(@Param("q") String q,
			@Param("type") Report.ReportType type,
			@Param("hasType") boolean hasType,
			@Param("status") String status,
			@Param("hasStatus") boolean hasStatus,
			@Param("reporterId") Long reporterId,
			@Param("hasReporterId") boolean hasReporterId);
}