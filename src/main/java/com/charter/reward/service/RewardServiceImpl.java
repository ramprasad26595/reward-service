package com.charter.reward.service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import com.charter.reward.dto.CustomerResponse;
import com.charter.reward.dto.RewardSummaryResponse;
import com.charter.reward.entity.Customer;
import com.charter.reward.entity.PurchaseTransaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Default reward service implementation.
 */
@Service
@RequiredArgsConstructor
public class RewardServiceImpl implements RewardService {

	private final RewardCustomerFinder customerFinder;
	private final RewardTransactionFinder transactionFinder;
	private final RewardDateRangeValidator dateRangeValidator;
	private final RewardSummaryAssembler summaryAssembler;

	@Override
	public List<CustomerResponse> getCustomers() {
		return customerFinder.findAll().stream()
				.sorted(Comparator.comparing(Customer::getFullName))
				.map(this::toCustomerResponse)
				.toList();
	}

	@Override
	public RewardSummaryResponse calculateRewards(Long customerId, LocalDate startDate, LocalDate endDate) {
		dateRangeValidator.validate(startDate, endDate);
		Customer customer = customerFinder.findById(customerId);
		List<PurchaseTransaction> transactions = transactionFinder.findByCustomerIdAndDateRange(customerId, startDate,
				endDate);
		return summaryAssembler.assemble(customer, startDate, endDate, transactions);
	}

	private CustomerResponse toCustomerResponse(Customer customer) {
		return new CustomerResponse(customer.getId(), customer.getFullName(), customer.getEmail());
	}
}
