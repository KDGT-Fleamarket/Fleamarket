//資料P79
package com.example.fleamarket.service;

import java.io.IOException;
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
	private final CategoryService categoryService;
	private final CloudinaryService cloudinaryService;

	public ItemService(ItemRepository itemRepository, CategoryService categoryService,
			CloudinaryService cloudinaryService) {
		this.itemRepository = itemRepository;
		this.categoryService = categoryService;
		this.cloudinaryService = cloudinaryService;
	}

	public Page<Item> searchItems(String keyword, Long categoryId, int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
		String status = "出品中";

		if (keyword != null && !keyword.isEmpty() && categoryId != null) {
			return itemRepository.findByNameContainingIgnoreCaseAndCategoryIdAndStatus(keyword, categoryId, status,
					pageable);
		} else if (keyword != null && !keyword.isEmpty()) {
			return itemRepository.findByNameContainingIgnoreCaseAndStatus(keyword, status, pageable);
		} else if (categoryId != null) {
			return itemRepository.findByCategoryIdAndStatus(categoryId, status, pageable);
		} else {
			return itemRepository.findByStatus(status, pageable);
		}
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
}