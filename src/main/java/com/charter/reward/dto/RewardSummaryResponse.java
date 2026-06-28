package com.charter.reward.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * Customer reward summary for a requested date range.
 */
public record RewardSummaryResponse(
		CustomerResponse customerDetails,
		LocalDate startDate,
		LocalDate endDate,
		int transactionCount,
		List<TransactionDetailResponse> transactionDetails,
		List<MonthlyRewardResponse> monthlyRewardPoints,
		int totalRewardPoints) {
}
