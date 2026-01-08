// src/main/java/com/example/fleamarketsystem/repository/UserRepository.java
package com.example.fleamarket.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.fleamarket.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmailIgnoreCase(String email);

	Optional<User> findByEmail(String email);

	Optional<User> findByName(String name);

	@Query(value = """
			SELECT CAST(COALESCE(AVG(r.rating), 0) AS double precision)
			  FROM review r
			 WHERE r.seller_id   = :userId
			    OR r.reviewer_id = :userId
			""", nativeQuery = true)
	Double averageRatingForUser(@Param("userId") Long userId);

	// キーワード(name or email) かつ ロール かつ BAN状態 で検索
	@Query("SELECT u FROM User u WHERE " +
			"(lower(u.name) LIKE lower(concat('%', :q, '%')) OR lower(u.email) LIKE lower(concat('%', :q, '%'))) " +
			"AND (:role IS NULL OR u.role = :role) " +
			"AND (:banned IS NULL OR u.banned = :banned) " +
			"ORDER BY u.id DESC")
	List<User> searchUsers(@Param("q") String q, @Param("role") String role, @Param("banned") Boolean banned);

	// 全件
	List<User> findAllByOrderByIdDesc();
}