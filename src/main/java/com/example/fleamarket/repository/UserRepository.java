// src/main/java/com/example/fleamarketsystem/repository/UserRepository.java
package com.example.fleamarket.repository;

import java.time.LocalDateTime;
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

	// 検索用：キーワード(name or email) + ロール + BAN状態 で検索
	@Query("SELECT u FROM User u WHERE " +
			"(lower(u.name) LIKE lower(concat('%', :q, '%')) OR lower(u.email) LIKE lower(concat('%', :q, '%'))) " +
			"AND (:role IS NULL OR u.role = :role) " +
			"AND (:banned IS NULL OR u.banned = :banned) " +
			"ORDER BY u.id DESC")
	List<User> searchUsers(@Param("q") String q, @Param("role") String role, @Param("banned") Boolean banned);

	List<User> findAllByOrderByIdDesc();

	// 集計用：日別新規登録者数
	@Query("SELECT CAST(u.createdAt AS date) as day, COUNT(u.id) " +
			"FROM User u WHERE u.createdAt BETWEEN :start AND :end " +
			"GROUP BY CAST(u.createdAt AS date) ORDER BY day ASC")
	List<Object[]> countDailyRegistrations(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}