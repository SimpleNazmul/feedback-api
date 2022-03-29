package com.simplenazmul.com.feedbackapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_GATEWAY)
public class BadGatewayException extends RuntimeException {
	private static final long serialVersionUID = -2927762559336817363L;

	public BadGatewayException(String errorMessage) {
		super(errorMessage);
	}
}
