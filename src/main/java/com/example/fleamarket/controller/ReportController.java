//追加クラス
package com.example.fleamarket.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.fleamarket.entity.Report;
import com.example.fleamarket.entity.User;
import com.example.fleamarket.service.ReportService;
import com.example.fleamarket.service.UserService;

@Controller
@RequestMapping("/reports")
public class ReportController {
	private final ReportService reportService;
	private final UserService userService;

	public ReportController(ReportService reportService, UserService userService) {
		this.reportService = reportService;
		this.userService = userService;
	}

	@GetMapping("/create")
	public String createForm(Model model) {
		model.addAttribute("report", new Report());
		model.addAttribute("types", Report.ReportType.values());
		return "user/reports/create";
	}

	@PostMapping("/create")
	public String create(@ModelAttribute Report report, @AuthenticationPrincipal UserDetails userDetails) {
		User user = userService.getUserByEmail(userDetails.getUsername())
				.orElseThrow(() -> new RuntimeException("User not found"));
		reportService.saveReport(report, user);
		return "redirect:/my-page/reports";
	}

	@GetMapping("/detail/{id}")
	public String detail(@PathVariable("id") Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
		Report report = reportService.getReportById(id);

		// セキュリティチェック：他人の報告を見られないようにする
		if (!report.getReporter().getEmail().equalsIgnoreCase(userDetails.getUsername())) {
			return "redirect:/my-page/reports";
		}

		model.addAttribute("report", report);
		return "user/reports/detail";
	}

	@PostMapping("/{id}/complete")
	public String completeReport(@PathVariable("id") Long id) {
		reportService.completeReport(id);
		return "redirect:/my-page";
	}
}
