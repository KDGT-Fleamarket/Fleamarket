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

	// コンストラクタで受け取るように変更
	public SecurityConfig(CustomUserDetailsService customUserDetailsService,
			CustomAuthenticationSuccessHandler successHandler) {
		this.customUserDetailsService = customUserDetailsService;
		this.successHandler = successHandler;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		// bcrypt
		return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/login", "/register", "/terms/**", "/login?error", "/error", "/css/**",
								"/js/**")
						.permitAll()
						.requestMatchers("/admin/**").hasRole("ADMIN")
						.anyRequest().authenticated())
				.formLogin(form -> form
						.loginPage("/login")
						.loginProcessingUrl("/login")
						.successHandler(successHandler)
						.failureUrl("/login?error")
						.permitAll());
		// ログを仕込んだクラスを明示的にセットします
		http.userDetailsService(customUserDetailsService);

		return http.build();
	}
}