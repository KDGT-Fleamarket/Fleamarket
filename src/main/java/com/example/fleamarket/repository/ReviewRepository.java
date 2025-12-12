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
	// 出品者に紐づくレビュー一覧を取得
	List<Review> findBySeller(User seller);

	// 注文 ID に紐づくレビューを一件取得（レビューは注文に 1 件）
	Optional<Review> findByOrderId(Long orderId);

	// レビューワ（投稿者）別のレビュー一覧を取得
	List<Review> findByReviewer(User reviewer);
}
