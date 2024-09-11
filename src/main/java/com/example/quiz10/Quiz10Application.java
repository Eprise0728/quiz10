package com.example.quiz10;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

//�]�����ϥ� spring-boot-starter-security ���̿�A�n�ư��w�]���򥻦w���ʳ]�w(�b�K�n�J����)
//�ư��b�K�n�J���ҴN�O�[�W exclude = SecurityAutoConfiguration.class
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class Quiz10Application {

	public static void main(String[] args) {
		SpringApplication.run(Quiz10Application.class, args);
	}

	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedOrigins("http://localhost:5173") // �ھڻݭn�վ�
				.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS").allowedHeaders("*");
	}

}
