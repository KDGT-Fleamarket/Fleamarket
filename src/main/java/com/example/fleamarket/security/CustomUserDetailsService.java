package com.example.fleamarket.security;

import java.util.List;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.fleamarket.entity.User;
import com.example.fleamarket.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository users;

	public CustomUserDetailsService(UserRepository users) {
		this.users = users;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User u = users.findByEmailIgnoreCase(username)
				.orElseThrow(() -> {
					return new UsernameNotFoundException("User not found: " + username);
				});

		if (!u.isEnabled())
			throw new DisabledException("Account disabled");
		if (u.isBanned())
			throw new DisabledException("Account banned");

		return new org.springframework.security.core.userdetails.User(
				u.getEmail(),
				u.getPassword(),
				// DBのroleが "USER" なら "ROLE_USER" になる
				List.of(new SimpleGrantedAuthority("ROLE_" + u.getRole())));
	}
}