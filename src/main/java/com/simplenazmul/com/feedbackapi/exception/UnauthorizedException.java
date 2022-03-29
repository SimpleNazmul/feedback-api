package com.simplenazmul.com.feedbackapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UnauthorizedException extends RuntimeException {
	private static final long serialVersionUID = -2927762559336817363L;
	
	public UnauthorizedException(String errorMessage) {
		super(errorMessage);
	}
}
