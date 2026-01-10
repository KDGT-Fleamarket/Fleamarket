package com.example.fleamarket.security;

import java.time.LocalDate;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.fleamarket.entity.Terms;
import com.example.fleamarket.entity.User;
import com.example.fleamarket.repository.TermsRepository;
import com.example.fleamarket.repository.UserRepository;

@Component
public class TermsCheckInterceptor implements HandlerInterceptor {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private TermsRepository termsRepository;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		// ログインしていない、または静的ファイル/規約/ログイン/ログアウトならチェックしない
		String path = request.getRequestURI();
		if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken ||
				path.startsWith("/terms") || path.startsWith("/login") || path.startsWith("/logout")
				|| path.contains(".")) {
			return true;
		}

		Set<String> roles = AuthorityUtils.authorityListToSet(auth.getAuthorities());
		if (!roles.contains("ROLE_USER")) {
			return true; // USER以外のロールは規約チェックを免除
		}

		// 1. 最新の規約（施行日が今日以前で最新のもの）を取得
		Terms latest = termsRepository
				.findFirstByEffectiveDateLessThanEqualOrderByEffectiveDateDescTermsVersionDesc(LocalDate.now())
				.orElse(null);
		if (latest == null)
			return true; // 規約が1つもなければスルー

		// 2. ユーザーの最終同意日を取得
		User user = userRepository.findByEmailIgnoreCase(auth.getName()).orElse(null);
		if (user != null) {
			// 同意日がない、または規約の施行日より前に同意していた場合は規約ページへ
			if (user.getTermsAgreedAt() == null
					|| user.getTermsAgreedAt().toLocalDate().isBefore(latest.getEffectiveDate())) {
				response.sendRedirect("/terms");
				return false;
			}
		}
		return true;
	}
}