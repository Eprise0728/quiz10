package com.example.quiz10;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

//因為有使用 spring-boot-starter-security 此依賴，要排除預設的基本安全性設定(帳密登入驗證)
//排除帳密登入驗證就是加上 exclude = SecurityAutoConfiguration.class
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class Quiz10Application {

	public static void main(String[] args) {
		SpringApplication.run(Quiz10Application.class, args);
	}

	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedOrigins("http://localhost:5173") // 根據需要調整
				.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS").allowedHeaders("*");
	}

}
