// リポジトリのパッケージ
package com.example.fleamarket.repository;

// コレクションやOptional用
import java.util.List;
import java.util.Optional;

// Spring Data JPA のimport
import org.springframework.data.jpa.repository.JpaRepository;
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
}