//資料P64
package com.example.fleamarket.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.fleamarket.entity.Review;
import com.example.fleamarket.entity.User;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
	List<Review> findBySeller(User seller);

	Optional<Review> findByOrderId(Long orderId);

	List<Review> findByReviewer(User reviewer);

	// 管理者向け：最新のレビューから順に全て取得
	List<Review> findAllByOrderByCreatedAtDesc();
}