package com.example.fleamarket.controller;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.fleamarket.entity.AppOrder;
import com.example.fleamarket.entity.Review;
import com.example.fleamarket.entity.User;
import com.example.fleamarket.service.AppOrderService;
import com.example.fleamarket.service.FavoriteService;
import com.example.fleamarket.service.ItemService;
import com.example.fleamarket.service.ReportService;
import com.example.fleamarket.service.ReviewService; // Add this import
import com.example.fleamarket.service.UserService;

@Controller
@RequestMapping("/my-page")
public class UserController {

	private final UserService userService;
	private final ItemService itemService;
	private final AppOrderService appOrderService;
	private final FavoriteService favoriteService;
	private final ReviewService reviewService;
	private final ReportService reportService;

	public UserController(UserService userService, ItemService itemService, AppOrderService appOrderService,
			FavoriteService favoriteService, ReviewService reviewService, ReportService reportService) {
		this.userService = userService;
		this.itemService = itemService;
		this.appOrderService = appOrderService;
		this.favoriteService = favoriteService;
		this.reviewService = reviewService;
		this.reportService = reportService;
	}

	@GetMapping
	public String myPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
		User currentUser = userService.getUserByEmail(userDetails.getUsername())
				.orElseThrow(() -> new RuntimeException("User not found"));

		model.addAttribute("noticeReports", reportService.getNotifiableReports(currentUser));
		model.addAttribute("noticeSales", appOrderService.getActionRequiredSales(currentUser));
		model.addAttribute("noticeOrders", appOrderService.getActionRequiredOrders(currentUser));

		model.addAttribute("user", currentUser);
		return "user/my_page";
	}

	@GetMapping("/selling")
	public String mySellingItems(@AuthenticationPrincipal UserDetails userDetails, Model model) {
		User currentUser = userService.getUserByEmail(userDetails.getUsername())
				.orElseThrow(() -> new RuntimeException("User not found"));

		model.addAttribute("sellingItems", itemService.getItemsBySeller(currentUser));
		return "user/selling/list";
	}

	@GetMapping("/orders")
	public String myOrders(@AuthenticationPrincipal UserDetails userDetails, Model model) {
		User currentUser = userService.getUserByEmail(userDetails.getUsername())
				.orElseThrow(() -> new RuntimeException("User not found"));

		List<AppOrder> orders = appOrderService.getOrdersByBuyer(currentUser);

		Map<Long, Review> reviewMap = new java.util.HashMap<>();
		for (AppOrder order : orders) {
			Review review = reviewService.getReviewByOrderId(order.getId());
			reviewMap.put(order.getId(), review);
		}

		model.addAttribute("myOrders", orders);
		model.addAttribute("reviewMap", reviewMap);
		return "user/orders/list";
	}

	@GetMapping("/orders/detail/{id}")
	public String orderDetail(@PathVariable("id") Long id, @AuthenticationPrincipal UserDetails userDetails,
			Model model) {
		AppOrder order = appOrderService.getOrderById(id);
		Review review = reviewService.getReviewByOrderId(id);

		model.addAttribute("order", order);
		model.addAttribute("review", review);
		return "user/orders/detail";
	}

	@GetMapping("/sales")
	public String mySales(@AuthenticationPrincipal UserDetails userDetails, Model model) {
		User currentUser = userService.getUserByEmail(userDetails.getUsername())
				.orElseThrow(() -> new RuntimeException("User not found"));

		List<AppOrder> sales = appOrderService.getOrdersBySeller(currentUser);

		Map<Long, Review> reviewMap = new java.util.HashMap<>();
		for (AppOrder order : sales) {
			Review review = reviewService.getReviewByOrderId(order.getId());
			reviewMap.put(order.getId(), review);
		}

		model.addAttribute("mySales", sales);
		model.addAttribute("reviewMap", reviewMap);
		return "user/sales/list";
	}

	@GetMapping("/sales/detail/{id}")
	public String salesDetail(@PathVariable("id") Long id, @AuthenticationPrincipal UserDetails userDetails,
			Model model) {
		AppOrder order = appOrderService.getOrderById(id);
		Review review = reviewService.getReviewByOrderId(id);

		model.addAttribute("order", order);
		model.addAttribute("review", review);
		return "user/sales/detail";
	}

	@GetMapping("/favorites")
	public String myFavorites(@AuthenticationPrincipal UserDetails userDetails, Model model) {
		User currentUser = userService.getUserByEmail(userDetails.getUsername())
				.orElseThrow(() -> new RuntimeException("User not found"));

		model.addAttribute("favoriteItems", favoriteService.getFavoriteItemsByUser(currentUser));
		return "user/favorites/list";
	}

	@GetMapping("/reviews")
	public String myReviews(@AuthenticationPrincipal UserDetails userDetails, Model model) {
		User currentUser = userService.getUserByEmail(userDetails.getUsername())
				.orElseThrow(() -> new RuntimeException("User not found"));

		model.addAttribute("reviews", reviewService.getReviewsByReviewer(currentUser));
		return "user/reviews/list";
	}

	@GetMapping("/reports")
	public String myReports(@AuthenticationPrincipal UserDetails userDetails, Model model) {
		User currentUser = userService.getUserByEmail(userDetails.getUsername())
				.orElseThrow(() -> new RuntimeException("User not found"));

		model.addAttribute("reports", reportService.getReportsByReporter(currentUser));

		return "user/reports/list";
	}
}