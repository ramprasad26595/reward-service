package com.reward.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionRewardResponse(
		Long transactionId,
		LocalDate transactionDate,
		BigDecimal amount,
		String merchantName,
		int points,
		RewardBreakdownResponse breakdown) {
}
