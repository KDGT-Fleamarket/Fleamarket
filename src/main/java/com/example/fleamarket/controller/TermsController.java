package com.example.fleamarket.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.fleamarket.entity.Terms;
import com.example.fleamarket.entity.User;
import com.example.fleamarket.repository.TermsRepository;
import com.example.fleamarket.repository.UserRepository;

@Controller
public class TermsController {
	private final TermsRepository termsRepository;
	private final UserRepository userRepository;

	public TermsController(TermsRepository termsRepository, UserRepository userRepository) {
		this.termsRepository = termsRepository;
		this.userRepository = userRepository;
	}

	@GetMapping("/terms")
	public String show(Model model) {
		// 現在有効な最新規約を表示
		Terms latest = termsRepository
				.findFirstByEffectiveDateLessThanEqualOrderByEffectiveDateDescTermsVersionDesc(LocalDate.now())
				.orElseThrow();
		model.addAttribute("terms", latest);
		return "user/terms";
	}

	@PostMapping("/terms/agree")
	public String agree(@AuthenticationPrincipal UserDetails userDetails) {
		User user = userRepository.findByEmailIgnoreCase(userDetails.getUsername()).orElseThrow();
		user.setTermsAgreedAt(LocalDateTime.now()); // 同意日時を更新
		userRepository.save(user);
		return "redirect:/items"; // 同意後は商品一覧へ
	}

	@GetMapping("/terms/view")
	public String viewTerms(Model model) {
		// 現在有効な最新規約を取得して表示
		termsRepository.findFirstByEffectiveDateLessThanEqualOrderByEffectiveDateDescTermsVersionDesc(LocalDate.now())
				.ifPresent(t -> model.addAttribute("terms", t));

		// 閲覧専用フラグを立てる
		model.addAttribute("isViewOnly", true);
		return "user/terms"; // 同じHTMLテンプレートを使い回す
	}
}