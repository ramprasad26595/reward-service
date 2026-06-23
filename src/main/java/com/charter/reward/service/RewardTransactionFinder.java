package com.charter.reward.service;

import com.charter.reward.entity.PurchaseTransaction;
import com.charter.reward.repository.PurchaseTransactionRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Reads customer transactions for a requested date range.
 */
@Component
@RequiredArgsConstructor
public class RewardTransactionFinder {

	private final PurchaseTransactionRepository transactionRepository;

	/**
	 * Finds transactions for a customer within the supplied range.
	 *
	 * @param customerId customer identifier
	 * @param startDate range start
	 * @param endDate range end
	 * @return matching transactions
	 */
	@Transactional(readOnly = true)
	public List<PurchaseTransaction> findByCustomerIdAndDateRange(Long customerId, LocalDate startDate,
			LocalDate endDate) {
		return transactionRepository.findByCustomerIdAndTransactionDateBetweenOrderByTransactionDateAsc(customerId,
				startDate, endDate);
	}
}
