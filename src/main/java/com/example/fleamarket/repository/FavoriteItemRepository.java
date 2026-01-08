package com.example.fleamarket.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.fleamarket.entity.FavoriteItem;
import com.example.fleamarket.entity.Item;
import com.example.fleamarket.entity.User;

@Repository
public interface FavoriteItemRepository extends JpaRepository<FavoriteItem, Long> {
	Optional<FavoriteItem> findByUserAndItem(User user, Item item);

	List<FavoriteItem> findByUserOrderByIdDesc(User user);

	boolean existsByUserAndItem(User user, Item item);
}