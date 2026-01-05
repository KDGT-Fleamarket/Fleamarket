package com.example.fleamarket.security;

import java.io.IOException;
import java.util.Set;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		// ログインしたユーザーが持っている権限（ROLE）を取得
		Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

		if (roles.contains("ROLE_ADMIN")) {
			// ADMINロールを持っていれば管理画面へ
			response.sendRedirect("/admin/dashboard");
		} else {
			// それ以外（GENERALなど）は商品一覧へ
			response.sendRedirect("/items");
		}
	}
}