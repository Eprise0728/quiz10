package com.example.quiz10.vo;

import java.util.List;

import com.example.quiz10.entity.Feedback;

public class FeedbackRes extends BasicRes{
	
	private List<Feedback> feedbackList;

	public FeedbackRes() {
		super();
		
	}

	public FeedbackRes(int code, String message) {
		super(code, message);
		
	}

	public FeedbackRes(int code, String message,List<Feedback> feedbackList) {
		super(code, message);
		this.feedbackList = feedbackList;
	}

	public List<Feedback> getFeedbackList() {
		return feedbackList;
	}
	
	
}
