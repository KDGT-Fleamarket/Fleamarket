package com.example.fleamarket.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.fleamarket.entity.User;
import com.example.fleamarket.repository.UserRepository;
import com.example.fleamarket.service.AppOrderService;
import com.example.fleamarket.service.ItemService;

@Controller
public class DashboardController {

	private final UserRepository userRepository;
	private final ItemService itemService;
	private final AppOrderService appOrderService;

	public DashboardController(UserRepository userRepository, ItemService itemService,
			AppOrderService appOrderService) {
		this.userRepository = userRepository;
		this.itemService = itemService;
		this.appOrderService = appOrderService;
	}

	@GetMapping("/admin/dashboard")
	public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
		User currentUser = userRepository.findByEmailIgnoreCase(userDetails.getUsername())
				.orElseThrow(() -> new RuntimeException("User not found"));

		if ("ADMIN".equals(currentUser.getRole())) {
			model.addAttribute("recentItems", itemService.getAllItems());
			model.addAttribute("recentOrders", appOrderService.getAllOrders());
			return "admin/dashboard";
		} else {
			return "redirect:/items";
		}
	}
}