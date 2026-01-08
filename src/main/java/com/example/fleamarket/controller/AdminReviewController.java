package com.example.fleamarket.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.fleamarket.entity.Review;
import com.example.fleamarket.service.ReviewService;

@Controller
@RequestMapping("/admin/reviews")
@PreAuthorize("hasRole('ADMIN')")
public class AdminReviewController {

	private final ReviewService reviewService;

	public AdminReviewController(ReviewService reviewService) {
		this.reviewService = reviewService;
	}

	@GetMapping
	public String list(@RequestParam(required = false) String q,
			@RequestParam(required = false) Integer rating,
			@RequestParam(required = false) Long sellerId,
			@RequestParam(required = false) Long reviewerId,
			Model model) {

		List<Review> reviews = reviewService.searchReviewsForAdmin(q, rating, sellerId, reviewerId);

		model.addAttribute("reviews", reviews);
		model.addAttribute("q", q);
		model.addAttribute("rating", rating);
		model.addAttribute("sellerId", sellerId);
		model.addAttribute("reviewerId", reviewerId);

		return "admin/reviews/list";
	}

	@GetMapping("/{id}")
	public String detail(@PathVariable Long id, Model model) {
		model.addAttribute("review", reviewService.findReviewById(id));
		return "admin/reviews/detail";
	}
}