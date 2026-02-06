package com.example.fleamarket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.fleamarket.security.CustomAuthenticationSuccessHandler;
import com.example.fleamarket.security.CustomUserDetailsService;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	private final CustomUserDetailsService customUserDetailsService;
	private final CustomAuthenticationSuccessHandler successHandler;

	public SecurityConfig(CustomUserDetailsService customUserDetailsService,
			CustomAuthenticationSuccessHandler successHandler) {
		this.customUserDetailsService = customUserDetailsService;
		this.successHandler = successHandler;
	}

	// パスワードをハッシュ化（BCrypt形式）するためのエンコーダーを定義
	@Bean
	public PasswordEncoder passwordEncoder() {
		// bcrypt
		return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
	}

	// URLごとのアクセス権限（認可）やログイン動作の詳細を設定
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/", "/login", "/register", "/terms/**", "/login?error", "/error", "/css/**",
								"/js/**", "/images/**", "/webjars/**")
						.permitAll()
						.requestMatchers("/admin/**").hasRole("ADMIN")
						.anyRequest().authenticated())
				.formLogin(form -> form
						.loginPage("/login")
						.loginProcessingUrl("/login")
						.successHandler(successHandler)
						.failureUrl("/login?error")
						.permitAll());
		http.userDetailsService(customUserDetailsService);

		return http.build();
	}
}