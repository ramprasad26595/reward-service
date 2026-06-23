package com.charter.reward.exception;

/**
 * Signals a client-side request validation failure.
 */
public class BadRequestException extends RuntimeException {

	public BadRequestException(String message) {
		super(message);
	}
}
