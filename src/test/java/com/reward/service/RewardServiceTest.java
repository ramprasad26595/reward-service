package com.reward.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.reward.dto.RewardSummaryResponse;
import com.reward.entity.Customer;
import com.reward.entity.PurchaseTransaction;
import com.reward.exception.BadRequestException;
import com.reward.exception.ResourceNotFoundException;
import com.reward.repository.CustomerRepository;
import com.reward.repository.PurchaseTransactionRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RewardServiceTest {

	@Mock
	private CustomerRepository customerRepository;

	@Mock
	private PurchaseTransactionRepository transactionRepository;

	private RewardServiceImpl rewardService;

	@BeforeEach
	void setUp() {
		rewardService = new RewardServiceImpl(customerRepository, transactionRepository, new RewardCalculator());
	}

	@Test
	void getCustomersReturnsSortedResults() {
		when(customerRepository.findAll()).thenReturn(List.of(
				new Customer("Rohan Mehta", "rohan.mehta@example.com"),
				new Customer("Aarav Sharma", "aarav.sharma@example.com"),
				new Customer("Priya Iyer", "priya.iyer@example.com")));

		assertThat(rewardService.getCustomers())
				.extracting("fullName")
				.containsExactly("Aarav Sharma", "Priya Iyer", "Rohan Mehta");
	}

	@Test
	void calculatesMonthlyAndTotalRewardsForDynamicDateRange() {
		Customer customer = customer(1L, "Aarav Sharma", "aarav.sharma@example.com");
		when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
		when(transactionRepository.findByCustomerIdAndTransactionDateBetweenOrderByTransactionDateAsc(eq(1L),
				eq(LocalDate.parse("2026-04-01")), eq(LocalDate.parse("2026-06-30"))))
				.thenReturn(List.of(
						transaction(customer, "2026-04-03", "84.25", "Reliance Fresh"),
						transaction(customer, "2026-04-21", "120.00", "Westside"),
						transaction(customer, "2026-05-09", "52.40", "Cafe Coffee Day"),
						transaction(customer, "2026-05-26", "211.75", "Pepperfry"),
						transaction(customer, "2026-06-12", "149.99", "Croma")));

		RewardSummaryResponse response = rewardService.calculateRewards(1L, LocalDate.parse("2026-04-01"),
				LocalDate.parse("2026-06-30"));

		assertThat(response.customerName()).isEqualTo("Aarav Sharma");
		assertThat(response.transactionCount()).isEqualTo(5);
		assertThat(response.totalPoints()).isEqualTo(546);
		assertThat(response.monthlyRewards()).extracting("month").containsExactly("2026-04", "2026-05", "2026-06");
		assertThat(response.monthlyRewards()).extracting("points").containsExactly(124, 274, 148);
		assertThat(response.monthlyRewards().get(0).transactions().get(1).breakdown().pointsOverOneHundred())
				.isEqualTo(40);
	}

	@Test
	void includesEveryMonthInTheRequestedRange() {
		Customer customer = customer(2L, "Priya Iyer", "priya.iyer@example.com");
		when(customerRepository.findById(2L)).thenReturn(Optional.of(customer));
		when(transactionRepository.findByCustomerIdAndTransactionDateBetweenOrderByTransactionDateAsc(eq(2L),
				eq(LocalDate.parse("2026-04-01")), eq(LocalDate.parse("2026-06-30"))))
				.thenReturn(List.of(
						transaction(customer, "2026-04-03", "75.40", "Grocery Mart"),
						transaction(customer, "2026-06-20", "120.00", "Camera Center")));

		RewardSummaryResponse response = rewardService.calculateRewards(2L, LocalDate.parse("2026-04-01"),
				LocalDate.parse("2026-06-30"));

		assertThat(response.transactionCount()).isEqualTo(2);
		assertThat(response.monthlyRewards()).extracting("month").containsExactly("2026-04", "2026-05", "2026-06");
		assertThat(response.monthlyRewards()).extracting("transactionCount").containsExactly(1, 0, 1);
		assertThat(response.monthlyRewards().get(1).totalSpend()).isEqualByComparingTo(BigDecimal.ZERO);
		assertThat(response.monthlyRewards().get(1).points()).isZero();
	}

	@Test
	void rejectsEndDateBeforeStartDate() {
		assertThatThrownBy(() -> rewardService.calculateRewards(1L, LocalDate.parse("2026-06-30"),
				LocalDate.parse("2026-04-01")))
				.isInstanceOf(BadRequestException.class)
				.hasMessage("endDate must be on or after startDate");
	}

	@Test
	void reportsMissingCustomersClearly() {
		when(customerRepository.findById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> rewardService.calculateRewards(99L, LocalDate.parse("2026-04-01"),
				LocalDate.parse("2026-06-30")))
				.isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("Customer 99 was not found");
	}

	private PurchaseTransaction transaction(Customer customer, String date, String amount, String merchant) {
		return new PurchaseTransaction(customer, LocalDate.parse(date), new BigDecimal(amount), merchant);
	}

	private Customer customer(Long id, String fullName, String email) {
		Customer customer = new Customer(fullName, email);
		try {
			java.lang.reflect.Field idField = Customer.class.getDeclaredField("id");
			idField.setAccessible(true);
			idField.set(customer, id);
			return customer;
		}
		catch (ReflectiveOperationException ex) {
			throw new IllegalStateException("Unable to set test customer id", ex);
		}
	}
}
