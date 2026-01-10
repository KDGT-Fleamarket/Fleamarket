//追加クラス
package com.example.fleamarket.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.fleamarket.entity.Terms;
import com.example.fleamarket.repository.TermsRepository;
import com.example.fleamarket.service.TermsService;

@Controller
@RequestMapping("/admin/terms")
public class AdminTermsController {

	private final TermsRepository termsRepository;
	private final TermsService termsService;

	public AdminTermsController(TermsRepository termsRepository, TermsService termsService) {
		this.termsRepository = termsRepository;
		this.termsService = termsService;
	}

	@GetMapping
	public String list(@RequestParam(required = false) String q,
			@RequestParam(required = false) String date,
			Model model) {

		List<Terms> terms = termsService.searchTermsForAdmin(q, date);

		model.addAttribute("terms", terms);
		model.addAttribute("q", q);
		model.addAttribute("date", date);

		return "admin/terms/list";
	}

	@GetMapping("/create")
	public String showCreateForm(Model model) {
		model.addAttribute("terms", new Terms());
		return "admin/terms/create";
	}

	@PostMapping("/create")
	public String createTerms(@ModelAttribute Terms terms) {
		termsRepository.save(terms);
		return "redirect:/admin/terms/list?success";
	}

	@GetMapping("/detail/{termsVersion}")
	public String detail(@PathVariable("termsVersion") Long termsVersion, Model model) {
		Terms terms = termsRepository.findById(termsVersion)
				.orElseThrow(() -> new IllegalArgumentException("Invalid terms Version:" + termsVersion));

		model.addAttribute("terms", terms);
		return "admin/terms/detail";
	}
}
