package com.charter.reward.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.charter.reward.dto.CustomerResponse;
import com.charter.reward.dto.MonthlyRewardResponse;
import com.charter.reward.dto.RewardSummaryResponse;
import com.charter.reward.entity.Customer;
import com.charter.reward.entity.PurchaseTransaction;
import com.charter.reward.exception.ResourceNotFoundException;
import com.charter.reward.mapper.RewardSummaryMapper;
import com.charter.reward.repository.CustomerRepository;
import com.charter.reward.repository.PurchaseTransactionRepository;
import com.charter.reward.validation.RewardDateRangeValidator;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RewardServiceImplTest {

	@Mock
	private CustomerRepository customerRepository;

	@Mock
	private PurchaseTransactionRepository transactionRepository;

	@Mock
	private RewardDateRangeValidator dateRangeValidator;

	@Mock
	private RewardSummaryMapper summaryMapper;

	@InjectMocks
	private RewardServiceImpl rewardService;

	@Test
	void getCustomersReturnsSortedCustomerResponses() {
		Customer alice = new Customer("Alice Smith", "alice@example.com");
		Customer bob = new Customer("Bob Jones", "bob@example.com");
		when(customerRepository.findAll()).thenReturn(List.of(bob, alice));

		List<CustomerResponse> result = rewardService.getCustomers();

		assertThat(result).extracting(CustomerResponse::fullName).containsExactly("Alice Smith", "Bob Jones");
	}

	@Test
	void calculateRewardsDelegatesToMapperAndReturnsResult() {
		Long customerId = 1L;
		LocalDate start = LocalDate.parse("2026-04-01");
		LocalDate end = LocalDate.parse("2026-04-30");

		Customer customer = new Customer("Aarav Sharma", "aarav@example.com");
		List<PurchaseTransaction> transactions = List.of(
				new PurchaseTransaction(customer, LocalDate.parse("2026-04-03"), new BigDecimal("84.25"), "Store"));
		RewardSummaryResponse expected = new RewardSummaryResponse(
				new CustomerResponse(null, "Aarav Sharma", "aarav@example.com"), start, end, 1,
				List.of(), List.of(new MonthlyRewardResponse(2026, "April", 1, new BigDecimal("84.25"), 34)),
				124);

		when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
		when(transactionRepository.findByCustomerIdAndTransactionDateBetweenOrderByTransactionDateAsc(customerId, start, end))
				.thenReturn(transactions);
		when(summaryMapper.assemble(customer, start, end, transactions)).thenReturn(expected);

		RewardSummaryResponse result = rewardService.calculateRewards(customerId, start, end);

		assertThat(result).isSameAs(expected);
		verify(dateRangeValidator).validate(start, end);
	}

	@Test
	void calculateRewardsThrowsNotFoundForUnknownCustomer() {
		when(customerRepository.findById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> rewardService.calculateRewards(99L, LocalDate.parse("2026-04-01"),
				LocalDate.parse("2026-04-30")))
				.isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("Customer 99 was not found");
	}
}
