package com.charter.reward.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * Customer reward summary for a requested date range.
 */
public record RewardSummaryResponse(
		Long customerId,
		String customerName,
		String email,
		LocalDate startDate,
		LocalDate endDate,
		int transactionCount,
		int totalPoints,
		List<MonthlyRewardResponse> monthlyRewards) {
}
