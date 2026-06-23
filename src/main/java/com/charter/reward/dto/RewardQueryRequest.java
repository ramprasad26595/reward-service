package com.charter.reward.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Query parameters for reward calculation.
 */
public record RewardQueryRequest(
		@NotBlank(message = "must not be blank")
		@Pattern(regexp = "\\d+", message = "must be a positive number")
		String customerId,

		@NotBlank(message = "must not be blank")
		@Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "must match yyyy-MM-dd")
		String startDate,

		@NotBlank(message = "must not be blank")
		@Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "must match yyyy-MM-dd")
		String endDate) {
}
