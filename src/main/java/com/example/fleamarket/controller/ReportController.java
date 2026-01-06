//追加クラス
package com.example.fleamarket.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.fleamarket.entity.Report;
import com.example.fleamarket.entity.User;
import com.example.fleamarket.repository.ReportRepository;
import com.example.fleamarket.repository.UserRepository;

@Controller
@RequestMapping("/reports")
public class ReportController {
	@Autowired
	private ReportRepository reportRepository;
	@Autowired
	private UserRepository userRepository;

	@GetMapping("/list")
	public String list(Model model, @AuthenticationPrincipal UserDetails userDetails) {
		User user = userRepository.findByEmailIgnoreCase(userDetails.getUsername()).orElseThrow();
		model.addAttribute("reports", reportRepository.findByReporterOrderByCreatedAtDesc(user));
		return "user/reports/list";
	}

	@GetMapping("/create")
	public String createForm(Model model) {
		model.addAttribute("report", new Report());
		model.addAttribute("types", Report.ReportType.values());
		return "user/reports/create";
	}

	@PostMapping("/create")
	public String create(Report report, @AuthenticationPrincipal UserDetails userDetails) {
		User user = userRepository.findByEmailIgnoreCase(userDetails.getUsername()).orElseThrow();
		report.setReporter(user);
		report.setStatus("未処理");
		report.setCreatedAt(LocalDateTime.now());
		reportRepository.save(report);
		return "redirect:/user/reports/list";
	}

	@GetMapping("/detail/{id}")
	public String detail(@PathVariable("id") Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
		Report report = reportRepository.findById(id).orElseThrow();

		// セキュリティチェック：他人の報告を見られないようにする
		if (!report.getReporter().getEmail().equalsIgnoreCase(userDetails.getUsername())) {
			return "redirect:/user/reports/list";
		}

		model.addAttribute("report", report);
		return "user/reports/detail";
	}
}
