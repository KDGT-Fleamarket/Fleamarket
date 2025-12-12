//資料P43
package com.example.fleamarket.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

	// ログインページ表示のハンドラ
	@GetMapping("/login")
	public String login() {
		// login.html（Thymeleaf）を返す
		return "login";
	}

}
