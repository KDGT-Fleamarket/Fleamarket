package com.example.fleamarket.security;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.fleamarket.entity.LoginLog;
import com.example.fleamarket.repository.LoginLogRepository;
import com.example.fleamarket.repository.UserRepository;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	private final LoginLogRepository loginLogRepository;
	private final UserRepository userRepository;

	public CustomAuthenticationSuccessHandler(LoginLogRepository loginLogRepository, UserRepository userRepository) {
		this.loginLogRepository = loginLogRepository;
		this.userRepository = userRepository;
	}

	// ログイン成功時に、最終ログイン日時の記録と権限（管理者/一般）に応じた画面遷移を行う
	@Override
	@Transactional
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		// 最終ログイン日時の更新
		String email = authentication.getName();
		userRepository.findByEmail(email).ifPresent(user -> {
			user.setLastLoginAt(LocalDateTime.now());
			loginLogRepository.save(new LoginLog(user));
			userRepository.save(user);
		});

		// 権限（ROLE）によるリダイレクト
		Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

		if (roles.contains("ROLE_ADMIN")) {
			response.sendRedirect("/admin/dashboard");
		} else {
			response.sendRedirect("/items");
		}
	}
}