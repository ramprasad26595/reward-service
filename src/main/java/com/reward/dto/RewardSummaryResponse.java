package com.reward.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record RewardSummaryResponse(
		Long customerId,
		String customerName,
		String email,
		LocalDate startDate,
		LocalDate endDate,
		int transactionCount,
		int totalPoints,
		List<MonthlyRewardResponse> monthlyRewards,
		Instant generatedAt) {
}
