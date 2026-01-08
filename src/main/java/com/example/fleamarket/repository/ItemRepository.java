package com.example.fleamarket.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.fleamarket.entity.Item;
import com.example.fleamarket.entity.User;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
	Page<Item> findByNameContainingIgnoreCaseAndStatus(String name, String status, Pageable pageable);

	Page<Item> findByCategoryIdAndStatus(Long categoryId, String status, Pageable pageable);

	Page<Item> findByNameContainingIgnoreCaseAndCategoryIdAndStatus(String name, Long categoryId, String status,
			Pageable pageable);

	Page<Item> findByStatus(String status, Pageable pageable);

	List<Item> findBySellerOrderByIdDesc(User seller);

	@Modifying
	@Transactional
	@Query("UPDATE Item i SET i.status = :newStatus WHERE i.id = :id AND i.status = '出品中'")
	int updateStatusIfAvailable(@Param("id") Long id, @Param("newStatus") String newStatus);
}