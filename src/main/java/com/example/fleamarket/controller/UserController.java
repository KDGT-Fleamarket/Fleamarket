package com.example.fleamarket.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

	@GetMapping("/profile/edit")
	public String editProfile(Principal principal, Model model) {
		User user = userService.findByEmailIgnoreCase(principal.getName()).orElseThrow();
		model.addAttribute("user", user);
		return "user/users/update";
	}

	@PostMapping("/profile/update")
	public String updateProfile(@RequestParam String name,
			@RequestParam String address,
			@RequestParam String email,
			@RequestParam(required = false) String password,
			Principal principal,
			HttpServletRequest request, // ログアウト用
			RedirectAttributes redirectAttributes) throws ServletException {

		User user = userService.findByEmailIgnoreCase(principal.getName()).orElseThrow();

		boolean isEmailChanged = !user.getEmail().equalsIgnoreCase(email);
		boolean isPasswordChanged = (password != null && !password.isBlank());

		// メールアドレス重複チェック
		if (isEmailChanged) {
			if (userService.getUserByEmail(email).isPresent()) {
				redirectAttributes.addFlashAttribute("errorMessage", "そのメールアドレスは既に登録されています。");
				return "redirect:/my-page/profile/edit";
			}
		}

		userService.updateProfile(user.getId(), name, address, email, password);

		// メールアドレスまたはパスワードが変わった場合は強制ログアウト
		if (isEmailChanged || isPasswordChanged) {
			request.logout();
			redirectAttributes.addFlashAttribute("successMessage", "認証情報が変更されました。新しい情報で再度ログインしてください。");
			return "redirect:/login";
		}
		// 名前や住所だけの更新ならマイページへ
		redirectAttributes.addFlashAttribute("successMessage", "アカウント情報を更新しました。");
		return "redirect:/my-page";
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