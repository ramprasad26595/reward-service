package com.charter.reward.dto;

import java.time.Instant;
import java.util.List;

/**
 * Standard error payload returned by the API.
 */
public record ApiErrorResponse(
		Instant timestamp,
		int status,
		String error,
		String message,
		String path,
		List<String> details) {
}
