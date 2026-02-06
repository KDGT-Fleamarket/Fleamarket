package com.example.fleamarket.repository;

import java.math.BigDecimal;
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
	List<Item> findBySellerOrderByIdDesc(User seller);

	@Modifying
	@Transactional
	@Query("UPDATE Item i SET i.status = :newStatus WHERE i.id = :id AND i.status = '出品中'")
	int updateStatusIfAvailable(@Param("id") Long id, @Param("newStatus") String newStatus);

	// 全件取得（ID降順）
	List<Item> findAllByOrderByIdDesc();

	// 集計用：日別出品数
	@Query("SELECT CAST(i.createdAt AS date) as day, COUNT(i.id) " +
			"FROM Item i WHERE i.createdAt BETWEEN :start AND :end " +
			"GROUP BY CAST(i.createdAt AS date) ORDER BY day ASC")
	List<Object[]> countDailyItems(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

	// 集計用：カテゴリ別出品数
	@Query("SELECT c.name, COUNT(i.id) " +
			"FROM Item i " +
			"JOIN i.category c " +
			"WHERE i.createdAt BETWEEN :start AND :end " +
			"GROUP BY c.name")
	List<Object[]> findItemCountByCategory(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

	// USER検索用：出品中 + キーワード + カテゴリ + 価格帯（ID降順）
	@Query("SELECT i FROM Item i WHERE " +
			"i.status = '出品中' AND " +
			"(:q IS NULL OR lower(i.name) LIKE lower(concat('%', CAST(:q AS text), '%')) OR lower(i.description) LIKE lower(concat('%', CAST(:q AS text), '%'))) AND "
			+
			"(:categoryId IS NULL OR i.category.id = :categoryId) AND " +
			"(:minPrice IS NULL OR i.price >= :minPrice) AND " +
			"(:maxPrice IS NULL OR i.price <= :maxPrice)" +
			"ORDER BY i.id DESC")
	Page<Item> searchForUser(@Param("q") String q,
			@Param("categoryId") Long categoryId,
			@Param("minPrice") BigDecimal minPrice,
			@Param("maxPrice") BigDecimal maxPrice,
			Pageable pageable);

	// ADMIN検索用：キーワード + ステータス + カテゴリ + 価格帯 (ID降順)
	@Query("SELECT i FROM Item i WHERE " +
			"(:q IS NULL OR lower(i.name) LIKE lower(concat('%', CAST(:q AS text), '%')) OR lower(i.description) LIKE lower(concat('%', CAST(:q AS text), '%'))) AND "
			+
			"(:status IS NULL OR i.status = :status) AND " +
			"(:categoryId IS NULL OR i.category.id = :categoryId) AND " +
			"(:minPrice IS NULL OR i.price >= :minPrice) AND " +
			"(:maxPrice IS NULL OR i.price <= :maxPrice) " +
			"ORDER BY i.id DESC")
	List<Item> searchForAdmin(@Param("q") String q,
			@Param("status") String status,
			@Param("categoryId") Long categoryId,
			@Param("minPrice") BigDecimal minPrice,
			@Param("maxPrice") BigDecimal maxPrice);
}