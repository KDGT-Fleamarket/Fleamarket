//追加クラス
package com.example.fleamarket.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.fleamarket.entity.Report;
import com.example.fleamarket.entity.User;
import com.example.fleamarket.repository.ReportRepository;

@Service
@Transactional
public class ReportService {

	private final ReportRepository reportRepository;

	public ReportService(ReportRepository reportRepository) {
		this.reportRepository = reportRepository;
	}

	// ユーザー用：自分の報告一覧
	public List<Report> getReportsByReporter(User reporter) {
		return reportRepository.findByReporterOrderByCreatedAtDesc(reporter);
	}

	// ユーザー用：新規投稿
	public void saveReport(Report report, User reporter) {
		report.setReporter(reporter);
		report.setStatus("未処理");
		report.setCreatedAt(LocalDateTime.now());
		reportRepository.save(report);
	}

	// 管理者用：全件取得
	public List<Report> getAllReportsForAdmin() {
		return reportRepository.findAllWithReporterOrderByCreatedAtDesc();
	}

	// IDによる1件取得
	public Report getReportById(Long id) {
		return reportRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Report not found"));
	}

	// 管理者用：更新（返信・ステータス変更）
	public void updateReportForAdmin(Long id, String status, String message) {
		Report report = reportRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Report not found"));

		report.setStatus(status);
		report.setMessage(message);
		reportRepository.save(report);
	}
}
