package com.charter.reward.service;

import com.charter.reward.dto.CustomerResponse;
import com.charter.reward.dto.RewardSummaryResponse;
import com.charter.reward.entity.Customer;
import com.charter.reward.entity.PurchaseTransaction;
import com.charter.reward.exception.ResourceNotFoundException;
import com.charter.reward.mapper.RewardSummaryMapper;
import com.charter.reward.repository.CustomerRepository;
import com.charter.reward.repository.PurchaseTransactionRepository;
import com.charter.reward.validation.RewardDateRangeValidator;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default reward service implementation.
 */
@Service
@RequiredArgsConstructor
public class RewardServiceImpl implements RewardService {

	private final CustomerRepository customerRepository;
	private final PurchaseTransactionRepository transactionRepository;
	private final RewardDateRangeValidator dateRangeValidator;
	private final RewardSummaryMapper summaryMapper;

	@Override
	@Transactional(readOnly = true)
	public List<CustomerResponse> getCustomers() {
		return customerRepository.findAll().stream()
				.sorted(Comparator.comparing(Customer::getFullName))
				.map(this::toCustomerResponse)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public RewardSummaryResponse calculateRewards(Long customerId, LocalDate startDate, LocalDate endDate) {
		dateRangeValidator.validate(startDate, endDate);
		Customer customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new ResourceNotFoundException("Customer " + customerId + " was not found"));
		List<PurchaseTransaction> transactions = transactionRepository
				.findByCustomerIdAndTransactionDateBetweenOrderByTransactionDateAsc(customerId, startDate, endDate);
		return summaryMapper.assemble(customer, startDate, endDate, transactions);
	}

	private CustomerResponse toCustomerResponse(Customer customer) {
		return new CustomerResponse(customer.getId(), customer.getFullName(), customer.getEmail());
	}
}
