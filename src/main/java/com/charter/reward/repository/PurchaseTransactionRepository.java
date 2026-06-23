package com.charter.reward.repository;

import com.charter.reward.entity.PurchaseTransaction;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Purchase transaction persistence access.
 */
public interface PurchaseTransactionRepository extends JpaRepository<PurchaseTransaction, Long> {

	List<PurchaseTransaction> findByCustomerIdAndTransactionDateBetweenOrderByTransactionDateAsc(
			Long customerId, LocalDate startDate, LocalDate endDate);
}
