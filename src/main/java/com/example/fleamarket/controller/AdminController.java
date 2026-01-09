package com.example.fleamarket.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.fleamarket.entity.Item;
import com.example.fleamarket.service.AppOrderService;
import com.example.fleamarket.service.ItemService;
import com.example.fleamarket.service.StatisticsService;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

	private final ItemService itemService;
	private final AppOrderService appOrderService;
	private final StatisticsService statisticsService;

	public AdminController(ItemService itemService, AppOrderService appOrderService,
			StatisticsService statisticsService) {
		this.itemService = itemService;
		this.appOrderService = appOrderService;
		this.statisticsService = statisticsService;
	}

	@GetMapping("/items")
	public String manageItems(@RequestParam(required = false) String q,
			@RequestParam(required = false) String status,
			Model model) {

		List<Item> items = itemService.searchItemsForAdmin(q, status);

		model.addAttribute("items", items);
		model.addAttribute("q", q); // 検索窓の値を保持
		model.addAttribute("status", status); // セレクトボックスの値を保持

		return "admin/items/list";
	}

	@PostMapping("/items/{id}/delete")
	public String deleteItemByAdmin(@PathVariable("id") Long itemId) {
		itemService.deleteItem(itemId);
		return "redirect:/admin/items?success=deleted";
	}

	@GetMapping("/statistics")
	public String showStatistics(
			@RequestParam(value = "startDate", required = false)
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(value = "endDate", required = false)
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
			Model model) {

		if (startDate == null)
			startDate = LocalDate.now().minusMonths(1);
		if (endDate == null)
			endDate = LocalDate.now();

		Map<String, Object> revenueChart = statisticsService.getRevenueChartData(startDate, endDate);
		Map<String, Object> activityChart = statisticsService.getActivityChartData(startDate, endDate);
		Map<String, Object> categorySales = statisticsService.getCategoryChartData(startDate, endDate);
		Map<String, Object> categoryItems = statisticsService.getItemCategoryChartData(startDate, endDate);
		Map<String, Object> activeUserChart = statisticsService.getActiveUserChartData(startDate, endDate);

		model.addAttribute("startDate", startDate);
		model.addAttribute("endDate", endDate);
		model.addAttribute("totalSales", appOrderService.getTotalSales(startDate, endDate));
		model.addAttribute("orderCountByStatus", appOrderService.getOrderCountByStatus(startDate, endDate));

		model.addAttribute("revenueLabels", revenueChart.get("labels"));
		model.addAttribute("revenueSales", revenueChart.get("sales"));
		model.addAttribute("revenueCounts", revenueChart.get("counts"));

		model.addAttribute("activityLabels", activityChart.get("labels"));
		model.addAttribute("activityUsers", activityChart.get("users"));
		model.addAttribute("activityItems", activityChart.get("items"));

		model.addAttribute("catSalesLabels", categorySales.get("labels"));
		model.addAttribute("catSalesData", categorySales.get("data"));

		model.addAttribute("catItemsLabels", categoryItems.get("labels"));
		model.addAttribute("catItemsData", categoryItems.get("data"));

		model.addAttribute("activeUserLabels", activeUserChart.get("labels"));
		model.addAttribute("activeUserData", activeUserChart.get("data"));

		return "admin/statistics";
	}

	@GetMapping("/statistics/csv")
	public void exportStatisticsCsv(
			@RequestParam(value = "startDate", required = false)
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(value = "endDate", required = false)
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
			HttpServletResponse response) throws IOException {

		if (startDate == null)
			startDate = LocalDate.now().minusMonths(1);
		if (endDate == null)
			endDate = LocalDate.now();

		response.setContentType("text/csv; charset=UTF-8");
		response.setHeader("Content-Disposition", "attachment; filename=\"flea_market_statistics.csv\"");

		try (PrintWriter writer = response.getWriter()) {
			writer.append("統計期間: ").append(String.valueOf(startDate)).append(" から ").append(String.valueOf(endDate))
					.append("\n\n");
			writer.append("総売上: ").append(String.valueOf(appOrderService.getTotalSales(startDate, endDate)))
					.append("\n\n");
			writer.append("ステータス別注文数\n");
			appOrderService.getOrderCountByStatus(startDate, endDate)
					.forEach((status, count) -> writer.append(status).append(",").append(String.valueOf(count))
							.append("\n"));
		}
	}
}