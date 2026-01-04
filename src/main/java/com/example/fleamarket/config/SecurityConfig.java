package com.example.fleamarket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.fleamarket.repository.UserRepository;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	@Bean
	public PasswordEncoder passwordEncoder() {
		// {bcrypt},{noop} など委譲エンコーダ
		return NoOpPasswordEncoder.getInstance();
	}

	@Bean
	public UserDetailsService userDetailsService(UserRepository userRepository) {
		return username -> {
			System.out.println("★★★ ログイン試行中: " + username);
			com.example.fleamarket.entity.User user = userRepository.findByEmail(username)
					.orElseThrow(() -> {
						System.out.println("★★★ ユーザーが見つかりません: " + username);
						return new org.springframework.security.core.userdetails.UsernameNotFoundException("Not found");
					});

			System.out.println("★★★ DBから取得したパスワード: [" + user.getPassword() + "]");
			System.out.println("★★★ DBから取得したロール: [" + user.getRole() + "]");

			return org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
					.password(user.getPassword())
					.roles(user.getRole().replace("ROLE_", "")) // roles()は自動でROLE_を足すので除去して渡す
					.build();
		};
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(
								"/login",
								"/css/**", "/js/**", "/images/**", "/webjars/**")
						.permitAll()
						.requestMatchers("/admin/**").hasRole("ADMIN")
						.anyRequest().authenticated())
				.formLogin(form -> form
						.loginPage("/login")
						.defaultSuccessUrl("/items", true) // ログイン成功後
						.permitAll())
				.logout(logout -> logout
						.logoutUrl("/logout") // POST /logout
						.logoutSuccessUrl("/login?logout")
						.permitAll())
				.csrf(Customizer.withDefaults());

		return http.build();
	}
}