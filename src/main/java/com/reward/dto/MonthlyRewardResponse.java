package com.reward.dto;

import java.math.BigDecimal;
import java.util.List;

public record MonthlyRewardResponse(
		String month,
		int transactionCount,
		BigDecimal totalSpend,
		int points,
		List<TransactionRewardResponse> transactions) {
}
