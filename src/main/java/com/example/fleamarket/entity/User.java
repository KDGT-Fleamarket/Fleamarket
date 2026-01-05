// src/main/java/com/example/fleamarketsystem/entity/User.java
package com.example.fleamarket.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 50)
	private String name;

	@Column(unique = true, nullable = false, length = 255)
	private String email;

	@Column(nullable = false, length = 255)
	private String password;

	@Column(nullable = false, length = 20)
	private String role; // "USER" or "ADMIN"

	@Column(nullable = false)
	private boolean enabled = true;

	@Column(nullable = false)
	private boolean banned = false;

	@Column(name = "ban_reason")
	private String banReason;

	@Column(name = "banned_at")
	private LocalDateTime bannedAt;

	@Column(name = "banned_by_admin_id")
	private Integer bannedByAdminId;

	@Column(name = "line_id")
	private String lineId;

	@Column(length = 255)
	private String address;

	@Column(name = "last_login_at", nullable = false)
	private LocalDateTime lastLoginAt = LocalDateTime.now();

	@Column(name = "terms_agreed_at")
	private LocalDateTime termsAgreedAt;
}