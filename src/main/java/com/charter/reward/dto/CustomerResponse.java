package com.charter.reward.dto;

/**
 * Lightweight customer response.
 */
public record CustomerResponse(
		Long customerId,
		String fullName,
		String email) {
}
