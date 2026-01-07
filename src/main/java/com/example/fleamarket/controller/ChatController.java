package com.example.fleamarket.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.fleamarket.entity.Chat;
import com.example.fleamarket.entity.User;
import com.example.fleamarket.service.ChatService;
import com.example.fleamarket.service.ItemService;
import com.example.fleamarket.service.UserService;

@Controller
@RequestMapping("/chat")
public class ChatController {

	private final ChatService chatService;
	private final ItemService itemService;
	private final UserService userService;
	private final SimpMessagingTemplate messagingTemplate;

	public ChatController(ChatService chatService, ItemService itemService, UserService userService,
			SimpMessagingTemplate messagingTemplate) {
		this.chatService = chatService;
		this.itemService = itemService;
		this.userService = userService;
		this.messagingTemplate = messagingTemplate;
	}

	@GetMapping("/{itemId}")
	public String showChatScreen(@PathVariable("itemId") Long itemId, Model model) {
		model.addAttribute("item", itemService.getItemById(itemId)
				.orElseThrow(() -> new RuntimeException("Item not found")));
		model.addAttribute("chats", chatService.getChatMessagesByItem(itemId));
		return "user/items/detail"; // Re-use item_detail for chat display
	}

	@PostMapping("/{itemId}")
	public String sendMessage(
			@PathVariable("itemId") Long itemId,
			@AuthenticationPrincipal UserDetails userDetails,
			@RequestParam("message") String message) {
		User sender = userService.getUserByEmail(userDetails.getUsername())
				.orElseThrow(() -> new RuntimeException("Sender not found"));
		chatService.sendMessage(itemId, sender, message);
		return "redirect:/chat/{itemId}";
	}

	@MessageMapping("/chat/{itemId}")
	public void handleChatMessage(@DestinationVariable Long itemId, ChatMessage messageDTO) {
		// ユーザー取得
		User sender = userService.getUserByEmail(messageDTO.getSenderEmail())
				.orElseThrow(() -> new RuntimeException("User not found"));

		// DB保存 (ChatServiceのsendMessageを活用)
		Chat savedChat = chatService.sendMessage(itemId, sender, messageDTO.getContent());

		// 全員に配信するデータを整形 (受信側で使う名前や日時をセット)
		messageDTO.setSenderName(sender.getName());
		messageDTO.setCreatedAt(
				java.time.format.DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm").format(savedChat.getCreatedAt()));

		messagingTemplate.convertAndSend("/topic/item/" + itemId, messageDTO);
	}
}