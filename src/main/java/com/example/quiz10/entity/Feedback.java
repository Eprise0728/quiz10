package com.example.quiz10.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "feedback")
@IdClass(value = FeedbackId.class)
public class Feedback {
	
	@Min(value= 1,message = "QuizId must be greater than 0")
	@Id
	@Column(name = "quiz_id")
	private int quizId;
	
	@Min(value= 1,message = "Ques must be greater than 0")
	@Id
	@Column(name = "qu_id")
	private int quId;

	@NotBlank(message = "name cannot be null empty")
	@Column(name = "name")
	private String name;

	@Column(name = "phone")
	private String phone;

	@NotBlank(message = "Email cannot be null empty")
	@Id
	@Column(name = "email")
	private String email;
	
	@Min(value= 1,message = "Age Id cannot be negative")
	@Column(name = "age")
	private int age;
	
	@Column(name = "ans")
	private String ans;
	
	@Column(name = "fillin_date_time")
	private LocalDateTime fillinDateTime;
	
	public Feedback() {
		super();
	}

	public Feedback(int quizId, int quId, String name, String phone, String email, int age, String ans,LocalDateTime fillinDateTime) {
		super();
		this.quizId = quizId;
		this.quId = quId;
		this.name = name;
		this.phone = phone;
		this.email = email;
		this.age = age;
		this.ans = ans;
		this.fillinDateTime = fillinDateTime;
	}

	public int getQuizId() {
		return quizId;
	}

	public int getQuId() {
		return quId;
	}

	public String getName() {
		return name;
	}

	public String getPhone() {
		return phone;
	}

	public String getEmail() {
		return email;
	}

	public int getAge() {
		return age;
	}

	public String getAns() {
		return ans;
	}

	public LocalDateTime getFillinDateTime() {
		return fillinDateTime;
	}

}
