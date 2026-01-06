//追加クラス
package com.example.fleamarket.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.fleamarket.entity.Terms;
import com.example.fleamarket.repository.TermsRepository;

@Controller
@RequestMapping("/admin/terms")
public class AdminTermsController {

	private final TermsRepository termsRepository;

	public AdminTermsController(TermsRepository termsRepository) {
		this.termsRepository = termsRepository;
	}

	// 規約の更新履歴一覧
	@GetMapping("/list")
	public String listTerms(Model model) {
		model.addAttribute("termsList", termsRepository.findAllByOrderByTermsVersionDesc());
		return "admin/terms/list";
	}

	// 新規規約の作成フォーム
	@GetMapping("/create")
	public String showCreateForm(Model model) {
		model.addAttribute("terms", new Terms());
		return "admin/terms/create";
	}

	// 新規規約の保存
	@PostMapping("/create")
	public String createTerms(@ModelAttribute Terms terms) {
		// terms.getEffectiveDate() で管理者が設定した施行日時が保存される
		termsRepository.save(terms);
		return "redirect:/admin/terms/list?success";
	}

	@GetMapping("/detail/{termsVersion}")
	public String detail(@PathVariable("termsVersion") Long termsVersion, Model model) {
		// findById は内部的に @Id（terms_version）を使って検索します
		Terms terms = termsRepository.findById(termsVersion)
				.orElseThrow(() -> new IllegalArgumentException("Invalid terms Version:" + termsVersion));

		model.addAttribute("terms", terms);
		return "admin/terms/detail";
	}
}
