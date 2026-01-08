package com.example.fleamarket.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.fleamarket.entity.User;
import com.example.fleamarket.service.AdminUserService;
import com.example.fleamarket.service.UserService;

@Controller
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

	private final AdminUserService adminService;
	private final UserService userService;

	public AdminUserController(AdminUserService adminService, UserService userService) {
		this.adminService = adminService;
		this.userService = userService;
	}

	@GetMapping
	public String list(@RequestParam(required = false) String q,
			@RequestParam(required = false) String role,
			@RequestParam(required = false) Boolean banned,
			Model model) {

		List<User> users = adminService.searchUsersForAdmin(q, role, banned);

		model.addAttribute("users", users);
		model.addAttribute("q", q);
		model.addAttribute("role", role);
		model.addAttribute("banned", banned);
		return "admin/users/list";
	}

	@GetMapping("/{id}")
	public String detail(@PathVariable Long id, Model model) {
		User user = userService.getUserById(id).orElseThrow();
		model.addAttribute("user", user);
		model.addAttribute("avgRating", adminService.averageRating(id));
		model.addAttribute("complaintCount", adminService.complaintCount(id));
		model.addAttribute("complaints", adminService.complaints(id));
		return "admin/users/detail";
	}

	@PostMapping("/{id}/ban")
	public String ban(@PathVariable Long id,
			@RequestParam("reason") String reason,
			@RequestParam(value = "disableLogin", defaultValue = "true") boolean disableLogin,
			Authentication auth) {

		User admin = userService.getUserByEmail(auth.getName()).orElseThrow();
		adminService.banUser(id, admin.getId(), reason, disableLogin);
		return "redirect:/admin/users/" + id + "?banned";
	}

	@PostMapping("/{id}/unban")
	public String unban(@PathVariable Long id) {
		adminService.unbanUser(id);
		return "redirect:/admin/users/" + id + "?unbanned";
	}

	@GetMapping("/create")
	public String showCreateForm(Model model) {
		model.addAttribute("user", new User());
		return "admin/users/create";
	}

	@PostMapping("/create")
	public String createAdminUser(User user) {
		userService.createAdminUser(user);
		return "redirect:/admin/users?created";
	}
}