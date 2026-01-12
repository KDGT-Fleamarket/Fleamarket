package com.example.fleamarket.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.fleamarket.entity.Chat;
import com.example.fleamarket.entity.Item;
import com.example.fleamarket.entity.User;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
	List<Chat> findByItemOrderByCreatedAtAsc(Item item);

	// 未読商品リスト
	@Query("SELECT DISTINCT c.item FROM Chat c " +
			"LEFT JOIN ChatRoomStatus s ON s.item = c.item AND s.user = :user " +
			"WHERE (c.item.seller = :user OR EXISTS (SELECT 1 FROM Chat c2 WHERE c2.item = c.item AND c2.sender = :user)) "
			+
			"AND c.sender <> :user " +
			"AND c.createdAt > COALESCE(s.lastViewedAt, '2000-01-01 00:00:00')")
	List<Item> findItemsWithUnreadMessages(@Param("user") User user);
}