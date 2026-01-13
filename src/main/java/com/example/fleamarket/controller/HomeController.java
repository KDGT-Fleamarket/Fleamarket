package com.example.fleamarket.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	@GetMapping("/")
	public String home(Authentication auth) {
		// 未ログインは /login に逃がす
		if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
			return "redirect:/login";
		}
		boolean isAdmin = auth.getAuthorities().stream()
				.anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
		return isAdmin ? "redirect:/admin/dashboard" : "redirect:/items";
	}
}