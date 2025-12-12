//資料P71
package com.example.fleamarket.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.fleamarket.entity.Category;
import com.example.fleamarket.repository.CategoryRepository;

@Service
public class CategoryService {

	private final CategoryRepository categoryRepository;

	public CategoryService(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	// すべてのカテゴリを取得
	public List<Category> getAllCategories() {
		// 全件取得を委譲
		return categoryRepository.findAll();
	}

	// 主キーでカテゴリを取得
	public Optional<Category> getCategoryById(Long id) {
		// Optional をそのまま返す
		return categoryRepository.findById(id);
	}

	// 名称でカテゴリを取得（名称は一意前提）
	public Optional<Category> getCategoryByName(String name) {
		// 名称検索を委譲
		return categoryRepository.findByName(name);
	}

	// 新規/更新保存
	public Category saveCategory(Category category) {
		// save に委譲
		return categoryRepository.save(category);
	}

	// 削除
	public void deleteCategory(Long id) {
		// ID 指定で削除
		categoryRepository.deleteById(id);
	}

}
