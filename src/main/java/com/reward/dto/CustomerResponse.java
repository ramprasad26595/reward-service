package com.reward.dto;

public record CustomerResponse(
		Long customerId,
		String fullName,
		String email) {
}
