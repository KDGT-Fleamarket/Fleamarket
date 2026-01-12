package com.example.fleamarket.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.fleamarket.entity.ChatRoomStatus;
import com.example.fleamarket.entity.Item;
import com.example.fleamarket.entity.User;

@Repository
public interface ChatRoomStatusRepository extends JpaRepository<ChatRoomStatus, Long> {
	Optional<ChatRoomStatus> findByUserAndItem(User user, Item item);
}