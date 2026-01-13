//資料P73
package com.example.fleamarket.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.fleamarket.entity.Chat;
import com.example.fleamarket.entity.ChatRoomStatus;
import com.example.fleamarket.entity.Item;
import com.example.fleamarket.entity.User;
import com.example.fleamarket.repository.ChatRepository;
import com.example.fleamarket.repository.ChatRoomStatusRepository;
import com.example.fleamarket.repository.ItemRepository;

@Service
public class ChatService {

	private final ChatRepository chatRepository;
	private final ChatRoomStatusRepository chatRoomStatusRepository;
	private final ItemRepository itemRepository;

	public ChatService(ChatRepository chatRepository, ChatRoomStatusRepository chatRoomStatusRepository,
			ItemRepository itemRepository) {
		this.chatRepository = chatRepository;
		this.chatRoomStatusRepository = chatRoomStatusRepository;
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

		updateLastViewed(sender, item);

		return savedChat;
	}

	@Transactional
	public void updateLastViewed(User user, Item item) {
		ChatRoomStatus status = chatRoomStatusRepository.findByUserAndItem(user, item)
				.orElseGet(() -> {
					ChatRoomStatus newStatus = new ChatRoomStatus();
					newStatus.setUser(user);
					newStatus.setItem(item);
					return newStatus;
				});

		status.setLastViewedAt(LocalDateTime.now());
		chatRoomStatusRepository.save(status);
	}

	public List<Item> getUnreadItemsForUser(User user) {
		return chatRepository.findItemsWithUnreadMessages(user);
	}

	public boolean hasChatHistory(Item item, User user) {
		return chatRepository.existsByItemAndUserInvolvement(item, user);
	}
}