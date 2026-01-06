package com.example.fleamarket.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.fleamarket.security.TermsCheckInterceptor;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	@Autowired
	private TermsCheckInterceptor termsCheckInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(termsCheckInterceptor);
	}
}