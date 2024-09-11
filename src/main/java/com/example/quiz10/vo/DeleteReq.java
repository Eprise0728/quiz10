package com.example.quiz10.vo;

import java.util.List;

import javax.validation.constraints.NotEmpty;

public class DeleteReq {
	
	@NotEmpty(message = "Quiz id list is empty!")
	private List<Integer> quizIdList;

	public List<Integer> getQuizIdList() {
		return quizIdList;
	}

	public void setQuizIdList(List<Integer> quizIdList) {
		this.quizIdList = quizIdList;
	}
	
}
