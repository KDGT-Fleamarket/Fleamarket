package com.example.fleamarket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class FleamarketApplication {

	public static void main(String[] args) {
		// .envファイルをロードしてシステムプロパティにセットする
		try {
			Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
			dotenv.entries().forEach(entry -> {
				System.setProperty(entry.getKey(), entry.getValue());
				// デバッグ用（起動時にコンソールに表示される。確認後消してください）
				if (entry.getKey().equals("DB_URL")) {
					System.out.println("Loaded DB_URL: " + entry.getValue());
				}
			});
		} catch (Exception e) {
			System.err.println(".env file not found or could not be loaded");
		}
		SpringApplication.run(FleamarketApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

}
