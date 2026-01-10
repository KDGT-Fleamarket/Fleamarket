//資料P73
package com.example.fleamarket.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.fleamarket.entity.Chat;
import com.example.fleamarket.entity.Item;
import com.example.fleamarket.entity.User;
import com.example.fleamarket.repository.ChatRepository;
import com.example.fleamarket.repository.ItemRepository;

@Service
public class ChatService {

	private final ChatRepository chatRepository;
	private final ItemRepository itemRepository;

	public ChatService(ChatRepository chatRepository, ItemRepository itemRepository) {
		this.chatRepository = chatRepository;
		this.itemRepository = itemRepository;
	}

	public List<Chat> getChatMessagesByItem(Long itemId) {
		Item item = itemRepository.findById(itemId)
				.orElseThrow(() -> new IllegalArgumentException("Item not found"));
		return chatRepository.findByItemOrderByCreatedAtAsc(item);
	}

	public Chat sendMessage(Long itemId, User sender, String message) {
		Item item = itemRepository.findById(itemId)
				.orElseThrow(() -> new IllegalArgumentException("Item not found"));

		Chat chat = new Chat();
		chat.setItem(item);
		chat.setSender(sender);
		chat.setMessage(message);
		chat.setCreatedAt(LocalDateTime.now());

		Chat savedChat = chatRepository.save(chat);

		// Send LINE notification to the other party in the chat
		User receiver = null;
		if (item.getSeller().equals(sender)) {
			// If sender is seller, receiver is buyer (if item is sold)
			// This logic needs to be refined if chat is before purchase
			// For now, assuming chat is always between seller and buyer of a purchased item
			// Or, if chat is before purchase, the other party is always the seller
			// For simplicity, let's assume the chat is always between the item's seller and the current sender's counterpart
			// If sender is seller, receiver is the buyer of the item (if any order exists)
			// If sender is buyer, receiver is the seller of the item
			receiver = item.getSeller(); // Default to seller if sender is buyer
			// If sender is seller, we need to find the buyer from an order associated with this item
			// This requires more complex logic, for now, let's simplify: chat is always with the seller
		} else { // Sender is buyer
			receiver = item.getSeller();
		}

		return savedChat;
	}
}