//資料P88
package com.example.fleamarket.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.fleamarket.entity.User;
import com.example.fleamarket.repository.UserRepository;

@Service
public class UserService {
	private final UserRepository repo;
	private final PasswordEncoder passwordEncoder;

	public UserService(UserRepository repo, PasswordEncoder passwordEncoder) {
		this.repo = repo;
		this.passwordEncoder = passwordEncoder;
	}

	public List<User> getAllUsers() {
		return repo.findAll(Sort.by(Sort.Direction.DESC, "id"));
	}

	public Optional<User> getUserById(Long id) {
		return repo.findById(id);
	}

	public Optional<User> getUserByEmail(String email) {
		return repo.findByEmail(email);
	}

	public User findByEmail(String email) {
		return repo.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("User not found with email: " + email));
	}

	@Transactional
	public User saveUser(User user) {
		return repo.save(user);
	}

	@Transactional
	public void deleteUser(Long id) {
		repo.deleteById(id);
	}

	@Transactional
	public void toggleUserEnabled(Long userId) {
		User u = repo.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
		u.setEnabled(!u.isEnabled());
		repo.save(u);
	}

	@Transactional
	public void registerNewUser(User user) {
		// パスワードをハッシュ化
		String encodedPassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodedPassword);

		// デフォルト値の設定
		user.setRole("USER");
		user.setEnabled(true);
		user.setBanned(false);

		repo.save(user);
	}

	@Transactional
	public void createAdminUser(User user) {
		// パスワードをハッシュ化
		String encodedPassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodedPassword);

		// 管理者用のデフォルト値設定
		user.setRole("ADMIN");
		user.setEnabled(true);
		user.setBanned(false);

		repo.save(user);
	}
}