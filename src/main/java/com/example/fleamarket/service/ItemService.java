//資料P79
package com.example.fleamarket.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.fleamarket.entity.Item;
import com.example.fleamarket.entity.User;
import com.example.fleamarket.repository.ItemRepository;

@Service
public class ItemService {

	private final ItemRepository itemRepository;
	private final CloudinaryService cloudinaryService;

	public ItemService(ItemRepository itemRepository,
			CloudinaryService cloudinaryService) {
		this.itemRepository = itemRepository;
		this.cloudinaryService = cloudinaryService;
	}

	public Page<Item> searchItems(String keyword, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, int page,
			int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
		String q = (keyword != null && !keyword.trim().isEmpty()) ? keyword : null;
		return itemRepository.searchForUser(q, categoryId, minPrice, maxPrice, pageable);
	}

	public List<Item> getAllItems() {
		return itemRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
	}

	public Optional<Item> getItemById(Long id) {
		return itemRepository.findById(id);
	}

	public Item saveItem(Item item, MultipartFile imageFile) throws IOException {
		if (imageFile != null && !imageFile.isEmpty()) {
			String imageUrl = cloudinaryService.uploadFile(imageFile);
			item.setImageUrl(imageUrl);
		}
		return itemRepository.save(item);
	}

	public void deleteItem(Long id) {
		itemRepository.findById(id).ifPresent(item -> {
			if (item.getImageUrl() != null) {
				try {
					cloudinaryService.deleteFile(item.getImageUrl());
				} catch (IOException e) {
					System.err.println("Failed to delete image from Cloudinary: " + e.getMessage());
				}
			}
			itemRepository.deleteById(id);
		});
	}

	public List<Item> getItemsBySeller(User seller) {
		return itemRepository.findBySellerOrderByIdDesc(seller);
	}

	public void markItemAsSold(Long itemId) {
		itemRepository.findById(itemId).ifPresent(item -> {
			item.setStatus("売却済");
			itemRepository.save(item);
		});
	}

	public void lockItemForPayment(Long itemId) {
		// ステータスを「決済待ち」に更新。成功すれば 1 が返る。
		int updatedCount = itemRepository.updateStatusIfAvailable(itemId, "決済待ち");

		if (updatedCount == 0) {
			// 更新できなかった＝既に誰かがステータスを変えてしまった
			throw new IllegalStateException("この商品は既に他のお客様が購入手続き中、または売却済みです。");
		}
	}

	public List<Item> searchItemsForAdmin(String q, String status, Long categoryId, BigDecimal minPrice,
			BigDecimal maxPrice) {
		String keyword = (q != null && !q.trim().isEmpty()) ? q : null;
		String statusFilter = (status != null && !status.trim().isEmpty()) ? status : null;
		return itemRepository.searchForAdmin(keyword, statusFilter, categoryId, minPrice, maxPrice);
	}
}