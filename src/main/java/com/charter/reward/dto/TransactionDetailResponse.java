package com.charter.reward.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Transaction-level reward breakdown.
 */
public record TransactionDetailResponse(
		Long transactionId,
		LocalDate transactionDate,
		String merchantName,
		BigDecimal amount,
		int points) {
}
