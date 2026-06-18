package com.reward.repository;

import com.reward.entity.PurchaseTransaction;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseTransactionRepository extends JpaRepository<PurchaseTransaction, Long> {

	List<PurchaseTransaction> findByCustomerIdAndTransactionDateBetweenOrderByTransactionDateAsc(
			Long customerId, LocalDate startDate, LocalDate endDate);
}
