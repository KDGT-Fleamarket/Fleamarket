package com.example.fleamarket.repository;

import java.time.LocalDateTime;
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

	// 全件取得（ID降順）
	List<Item> findAllByOrderByIdDesc();

	// 検索用 キーワード(name or description) ＋ ステータス
	@Query("SELECT i FROM Item i WHERE " +
			"(lower(i.name) LIKE lower(concat('%', :q, '%')) OR lower(i.description) LIKE lower(concat('%', :q, '%'))) "
			+
			"AND (:status IS NULL OR i.status = :status) " +
			"ORDER BY i.id DESC")
	List<Item> searchForAdmin(@Param("q") String q, @Param("status") String status);

	// 集計用：出品数
	@Query("SELECT CAST(i.createdAt AS date) as day, COUNT(i.id) " +
			"FROM Item i WHERE i.createdAt BETWEEN :start AND :end " +
			"GROUP BY CAST(i.createdAt AS date) ORDER BY day ASC")
	List<Object[]> countDailyItems(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

	// 集計用：カテゴリ
	@Query("SELECT c.name, COUNT(i.id) " +
			"FROM Item i " +
			"JOIN i.category c " +
			"WHERE i.createdAt BETWEEN :start AND :end " +
			"GROUP BY c.name")
	List<Object[]> findItemCountByCategory(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}