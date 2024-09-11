package com.example.quiz10;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.quiz10.repositort.QuizDao;

@SpringBootTest
class Quiz10ApplicationTests {
	
	@Autowired
	private QuizDao quizDao;
	
	@Test
	void contextLoads() {
		
	}

}
