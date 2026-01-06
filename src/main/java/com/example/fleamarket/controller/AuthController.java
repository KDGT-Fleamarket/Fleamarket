//資料P43
package com.example.fleamarket.controller;

import java.time.LocalDateTime;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.fleamarket.entity.User;
import com.example.fleamarket.repository.UserRepository;

@Controller
public class AuthController {

	private final UserRepository userRepository;

	public AuthController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@GetMapping("/login")
	public String login() {
		return "login"; // templates/login.html
	}

	@GetMapping("/register")
	public String showRegistrationForm(Model model) {
		model.addAttribute("user", new User());
		return "register";
	}

	@PostMapping("/register")
	public String registerUser(User user) {
		// デフォルト値の設定
		user.setRole("USER");
		user.setEnabled(true);
		user.setBanned(false);
		user.setLastLoginAt(LocalDateTime.now()); // 作成日時としてセット

		userRepository.save(user);
		return "redirect:/login?registered";
	}
}