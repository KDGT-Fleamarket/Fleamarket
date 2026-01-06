package com.example.fleamarket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.fleamarket.entity.Report;
import com.example.fleamarket.repository.ReportRepository;

@Controller
@RequestMapping("/admin/reports")
public class AdminReportController {
	@Autowired
	private ReportRepository reportRepository;

	@GetMapping("/list")
	public String list(Model model) {
		model.addAttribute("reports", reportRepository.findAllByOrderByCreatedAtDesc());
		return "admin/reports/list";
	}

	@GetMapping("/detail/{id}")
	public String detail(@PathVariable Long id, Model model) {
		model.addAttribute("report", reportRepository.findById(id).orElseThrow());
		return "admin/reports/detail";
	}

	@PostMapping("/update")
	public String update(@RequestParam Long id, @RequestParam String status, @RequestParam String message) {
		Report report = reportRepository.findById(id).orElseThrow();
		report.setStatus(status);
		report.setMessage(message); // 管理者が追記した内容を保存
		reportRepository.save(report);
		return "redirect:/admin/reports/detail/" + id;
	}
}
