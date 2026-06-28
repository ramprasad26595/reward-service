package com.charter.reward.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.charter.reward.RewardServiceApplication;
import com.charter.reward.dto.CustomerResponse;
import com.charter.reward.dto.RewardSummaryResponse;
import com.charter.reward.entity.Customer;
import com.charter.reward.entity.PurchaseTransaction;
import com.charter.reward.exception.BadRequestException;
import com.charter.reward.exception.ResourceNotFoundException;
import com.charter.reward.repository.CustomerRepository;
import com.charter.reward.repository.PurchaseTransactionRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

/**
 * Exercises the real service layer against seeded H2 data.
 */
@SpringBootTest(classes = RewardServiceApplication.class)
class RewardServiceIntegrationTest {

	@Autowired
	private RewardService rewardService;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private PurchaseTransactionRepository transactionRepository;

	@Test
	void returnsSortedCustomersFromTheDatabase() {
		List<CustomerResponse> customers = rewardService.getCustomers();

		assertThat(customers).extracting(CustomerResponse::fullName)
				.containsExactly("Aarav Sharma", "Priya Iyer", "Rohan Mehta");
	}

	@Test
	void calculatesRewardSummaryFromSeededData() {
		RewardSummaryResponse response = rewardService.calculateRewards(1L, LocalDate.parse("2026-04-01"),
				LocalDate.parse("2026-06-30"));

		assertThat(response.customerDetails().fullName()).isEqualTo("Aarav Sharma");
		assertThat(response.transactionCount()).isEqualTo(5);
		assertThat(response.transactionDetails()).hasSize(5);
		assertThat(response.totalRewardPoints()).isEqualTo(546);
		assertThat(response.monthlyRewardPoints()).extracting("month").containsExactly("April", "May", "June");
		assertThat(response.monthlyRewardPoints()).extracting("points").containsExactly(124, 274, 148);
	}

	@Test
	void exposesPerTransactionRewardBreakdown() {
		RewardSummaryResponse response = rewardService.calculateRewards(1L, LocalDate.parse("2026-04-01"),
				LocalDate.parse("2026-04-30"));

		// $84.25 -> 34 points (cents ignored), $120.00 -> 90 points
		assertThat(response.transactionDetails()).extracting("points").containsExactly(34, 90);
		assertThat(response.transactionDetails()).extracting("merchantName")
				.containsExactly("Reliance Fresh", "Westside");
	}

	@Test
	void returnsEmptyBreakdownWhenNoTransactionsInRange() {
		RewardSummaryResponse response = rewardService.calculateRewards(1L, LocalDate.parse("2020-01-01"),
				LocalDate.parse("2020-01-31"));

		assertThat(response.transactionCount()).isZero();
		assertThat(response.transactionDetails()).isEmpty();
		assertThat(response.totalRewardPoints()).isZero();
		assertThat(response.monthlyRewardPoints()).extracting("points").containsExactly(0);
	}

	@Test
	void supportsSameDayDateRange() {
		RewardSummaryResponse response = rewardService.calculateRewards(1L, LocalDate.parse("2026-04-21"),
				LocalDate.parse("2026-04-21"));

		assertThat(response.transactionCount()).isEqualTo(1);
		assertThat(response.totalRewardPoints()).isEqualTo(90);
	}

	@Test
	@Transactional
	void includesNewlyPersistedTransactionsInTheRealDatabaseFlow() {
		Customer customer = customerRepository.findById(1L).orElseThrow();
		transactionRepository.save(new PurchaseTransaction(customer, LocalDate.parse("2026-05-01"),
				new BigDecimal("50.00"), "Test Mart"));

		RewardSummaryResponse response = rewardService.calculateRewards(1L, LocalDate.parse("2026-04-01"),
				LocalDate.parse("2026-06-30"));

		assertThat(response.transactionCount()).isEqualTo(6);
		assertThat(response.totalRewardPoints()).isEqualTo(546);
		assertThat(response.monthlyRewardPoints().get(1).transactionCount()).isEqualTo(3);
	}

	@Test
	void throwsNotFoundForUnknownCustomer() {
		assertThatThrownBy(() -> rewardService.calculateRewards(99L, LocalDate.parse("2026-04-01"),
				LocalDate.parse("2026-06-30")))
				.isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("Customer 99 was not found");
	}

	@Test
	void throwsBadRequestForInvalidDateOrder() {
		assertThatThrownBy(() -> rewardService.calculateRewards(1L, LocalDate.parse("2026-06-30"),
				LocalDate.parse("2026-04-01")))
				.isInstanceOf(BadRequestException.class)
				.hasMessage("endDate must be on or after startDate");
	}
}
