package com.example.fleamarket.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.fleamarket.entity.Report;
import com.example.fleamarket.service.ReportService;

@Controller
@RequestMapping("/admin/reports")
public class AdminReportController {
	private final ReportService reportService;

	public AdminReportController(ReportService reportService) {
		this.reportService = reportService;
	}

	@GetMapping
	public String list(@RequestParam(required = false) String q,
			@RequestParam(required = false) String type,
			@RequestParam(required = false) String status,
			Model model) {

		List<Report> reports = reportService.searchReportsForAdmin(q, type, status);

		model.addAttribute("reports", reports);
		model.addAttribute("q", q);
		model.addAttribute("type", type);
		model.addAttribute("status", status);

		return "admin/reports/list";
	}

	@GetMapping("/detail/{id}")
	public String detail(@PathVariable Long id, Model model) {
		model.addAttribute("report", reportService.getReportById(id));
		return "admin/reports/detail";
	}

	@PostMapping("/update")
	public String update(@RequestParam Long id, @RequestParam String status, @RequestParam String message) {
		reportService.updateReportForAdmin(id, status, message);
		return "redirect:/admin/reports/detail/" + id;
	}
}
