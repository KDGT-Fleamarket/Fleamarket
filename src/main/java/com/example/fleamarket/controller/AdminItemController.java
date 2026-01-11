package com.example.fleamarket.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.fleamarket.entity.Chat;
import com.example.fleamarket.entity.Item;
import com.example.fleamarket.service.ChatService;
import com.example.fleamarket.service.ItemService;
import com.example.fleamarket.service.ReviewService;

@Controller
@RequestMapping("/admin/items")
@PreAuthorize("hasRole('ADMIN')")
public class AdminItemController {

	private final ItemService itemService;
	private final ChatService chatService;
	private final ReviewService reviewService;

	public AdminItemController(ItemService itemService, ChatService chatService, ReviewService reviewService) {
		this.itemService = itemService;
		this.chatService = chatService;
		this.reviewService = reviewService;
	}

	@GetMapping
	public String manageItems(@RequestParam(required = false) String q,
			@RequestParam(required = false) String status,
			Model model) {

		List<Item> items = itemService.searchItemsForAdmin(q, status);

		model.addAttribute("items", items);
		model.addAttribute("q", q);
		model.addAttribute("status", status);

		return "admin/items/list";
	}

	@GetMapping("/{id}")
	public String detail(@PathVariable Long id, Model model) {
		Item item = itemService.getItemById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid item Id:" + id));

		List<Chat> chats = chatService.getChatMessagesByItem(id);

		model.addAttribute("item", item);
		model.addAttribute("chats", chats);

		reviewService.getAverageRatingForSeller(item.getSeller())
				.ifPresent(avg -> model.addAttribute("sellerAverageRating", String.format("%.1f", avg)));

		return "admin/items/detail";
	}

	@PostMapping("/{id}/delete")
	public String deleteItemByAdmin(@PathVariable("id") Long itemId, RedirectAttributes redirectAttributes) {
		itemService.deleteItem(itemId);
		redirectAttributes.addFlashAttribute("successMessage", "商品を削除しました。");
		return "redirect:/admin/items?success=deleted";
	}

}
