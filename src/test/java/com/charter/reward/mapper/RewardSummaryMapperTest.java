package com.charter.reward.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.charter.reward.dto.RewardSummaryResponse;
import com.charter.reward.entity.Customer;
import com.charter.reward.entity.PurchaseTransaction;
import com.charter.reward.service.RewardCalculator;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class RewardSummaryMapperTest {

	private final RewardSummaryMapper mapper = new RewardSummaryMapper(new RewardCalculator());

	private final Customer customer = new Customer("Aarav Sharma", "aarav.sharma@example.com");

	@Test
	void assemblesCustomerAndTransactionDetails() {
		List<PurchaseTransaction> transactions = List.of(
				transaction("2026-04-03", "84.25", "Reliance Fresh"),
				transaction("2026-04-21", "120.00", "Westside"));

		RewardSummaryResponse response = mapper.assemble(customer, LocalDate.parse("2026-04-01"),
				LocalDate.parse("2026-04-30"), transactions);

		assertThat(response.customerDetails().fullName()).isEqualTo("Aarav Sharma");
		assertThat(response.transactionCount()).isEqualTo(2);
		assertThat(response.transactionDetails()).extracting("points").containsExactly(34, 90);
		assertThat(response.totalRewardPoints()).isEqualTo(124);
	}

	@Test
	void fillsEveryMonthInRangeEvenWithoutTransactions() {
		List<PurchaseTransaction> transactions = List.of(transaction("2026-04-21", "120.00", "Westside"));

		RewardSummaryResponse response = mapper.assemble(customer, LocalDate.parse("2026-04-01"),
				LocalDate.parse("2026-06-30"), transactions);

		assertThat(response.monthlyRewardPoints()).extracting("month").containsExactly("April", "May", "June");
		assertThat(response.monthlyRewardPoints()).extracting("points").containsExactly(90, 0, 0);
	}

	@Test
	void producesSingleEmptyMonthWhenNoTransactions() {
		RewardSummaryResponse response = mapper.assemble(customer, LocalDate.parse("2026-04-01"),
				LocalDate.parse("2026-04-30"), List.of());

		assertThat(response.transactionDetails()).isEmpty();
		assertThat(response.monthlyRewardPoints()).hasSize(1);
		assertThat(response.totalRewardPoints()).isZero();
	}

	private PurchaseTransaction transaction(String date, String amount, String merchant) {
		return new PurchaseTransaction(customer, LocalDate.parse(date), new BigDecimal(amount), merchant);
	}
}
