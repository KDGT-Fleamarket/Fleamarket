//資料P64
package com.example.fleamarket.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.fleamarket.entity.Review;
import com.example.fleamarket.entity.User;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
	List<Review> findBySellerOrderByIdDesc(User seller);

	Optional<Review> findByOrderId(Long orderId);

	List<Review> findByReviewerOrderByIdDesc(User reviewer);

	// 管理者向け：最新のレビューから順に全て取得
	List<Review> findAllByOrderByCreatedAtDesc();

	@Query("SELECT r FROM Review r WHERE " +
			"(lower(r.comment) LIKE lower(concat('%', :q, '%'))) " +
			"AND (:rating IS NULL OR r.rating = :rating) " +
			"AND (:sellerId IS NULL OR r.seller.id = :sellerId) " +
			"AND (:reviewerId IS NULL OR r.reviewer.id = :reviewerId) " +
			"ORDER BY r.id DESC")
	List<Review> searchReviews(@Param("q") String q,
			@Param("rating") Integer rating,
			@Param("sellerId") Long sellerId,
			@Param("reviewerId") Long reviewerId);
}