package com.charter.reward.dto;

import java.math.BigDecimal;

/**
 * Monthly reward summary.
 */
public record MonthlyRewardResponse(
		int year,
		String month,
		int transactionCount,
		BigDecimal totalSpend,
		int points) {
}
