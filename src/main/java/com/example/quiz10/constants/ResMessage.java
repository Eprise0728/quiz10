package com.example.quiz10.constants;

public enum ResMessage {
	
	SUCCESS(200,"Success!"),
	DATA_ERROR(400,"Start date cannot be later than end date!!"),
	OPTIONS_ERROR(400,"Options cannot be null or empty!!"),
	QUIZ_NOT_FOUND(404,"Quiz not found!"),
	QUIZ_ID_NOT_MATCH(400,"Quiz id not match"),
	QUIZ_IN_PROGRESS(400,"Quiz in progress"),
	QUIZ_ID_OR_EMAIL_INCONSISTENT(400,"Quiz id or email inconsistent"),
	EMAIL_DUPLICATE(400,"Email duplicate"),
	CANNOT_FILLIN_QUIZ(400,"Cannot fillin quiz"),
	FILLIN_INCOMPLETE(400,"Fillin incomplete"),
	FILLIN_IS_NECESSARY(400,"Fillin is necessary"),
	QUID_MISMATCH(400,"Quid mismatch"),
	SINGLE_CHOICE_QUES(400,"Single choice ques"),
	OPTION_ANSWER_MISMATCH(400,"Option answer mismatch"),
	USER_NAME_EXISTED(400,"User name existed"),
	USER_NAME_NOT_FOUND(404,"User name not found"),
	PASSWORD_INCONSISTENT(400,"Password inconsistent"),
	PLEASE_LOGIN_FIRST(400,"Please login first");
	private int code;

	private String message;

	private ResMessage(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

}
