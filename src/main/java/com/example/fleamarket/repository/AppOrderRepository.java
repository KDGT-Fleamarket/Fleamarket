// リポジトリのパッケージ
package com.example.fleamarket.repository;

import java.time.LocalDateTime;
// コレクションやOptional用
import java.util.List;
import java.util.Optional;

// Spring Data JPA のimport
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

// エンティティと関連型のimport
import com.example.fleamarket.entity.AppOrder;
import com.example.fleamarket.entity.User;

@Repository
public interface AppOrderRepository extends JpaRepository<AppOrder, Long> {
	// 買い手で注文一覧を取得
	List<AppOrder> findByBuyerOrderByIdDesc(User buyer);

	// 出品者で注文一覧を取得（Itemのseller経由）
	List<AppOrder> findByItem_SellerOrderByIdDesc(User seller);

	// PaymentIntent IDで1件を特定（決済完了時に使う）
	Optional<AppOrder> findByPaymentIntentId(String paymentIntentId);

	// 集計用：売上・取引件数
	@Query("SELECT CAST(o.createdAt AS date) as orderDay, SUM(o.price) as sales, COUNT(o.id) as count " +
			"FROM AppOrder o " +
			"WHERE o.createdAt BETWEEN :start AND :end " +
			"GROUP BY CAST(o.createdAt AS date) " +
			"ORDER BY CAST(o.createdAt AS date) ASC")
	List<Object[]> findDailySales(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

	// 集計用：カテゴリ
	@Query("SELECT c.name, SUM(o.price) " +
			"FROM AppOrder o " +
			"JOIN o.item i " +
			"JOIN i.category c " +
			"WHERE o.createdAt BETWEEN :start AND :end " +
			"GROUP BY c.name")
	List<Object[]> findSalesByCategory(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}