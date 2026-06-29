package com.charter.reward.mapper;

import com.charter.reward.dto.CustomerResponse;
import com.charter.reward.dto.MonthlyRewardResponse;
import com.charter.reward.dto.RewardSummaryResponse;
import com.charter.reward.dto.TransactionDetailResponse;
import com.charter.reward.entity.Customer;
import com.charter.reward.entity.PurchaseTransaction;
import com.charter.reward.service.RewardCalculator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Shapes transaction data into the public reward response.
 */
@Component
@RequiredArgsConstructor
public class RewardSummaryMapper {

	private final RewardCalculator rewardCalculator;

	/**
	 * Builds the final reward summary response.
	 *
	 * @param customer customer
	 * @param startDate requested start date
	 * @param endDate requested end date
	 * @param transactions matching transactions
	 * @return reward summary response
	 */
	public RewardSummaryResponse assemble(Customer customer, LocalDate startDate, LocalDate endDate,
			List<PurchaseTransaction> transactions) {
		List<TransactionDetailResponse> transactionDetails = buildTransactionDetails(transactions);
		List<MonthlyRewardResponse> monthlyRewards = buildMonthlyRewards(transactions, startDate, endDate);
		int totalPoints = monthlyRewards.stream().mapToInt(MonthlyRewardResponse::points).sum();
		CustomerResponse customerDetails = new CustomerResponse(customer.getId(), customer.getFullName(),
				customer.getEmail());
		return new RewardSummaryResponse(customerDetails, startDate, endDate, transactions.size(), transactionDetails,
				monthlyRewards, totalPoints);
	}

	private List<TransactionDetailResponse> buildTransactionDetails(List<PurchaseTransaction> transactions) {
		return transactions.stream()
				.map(transaction -> new TransactionDetailResponse(transaction.getId(), transaction.getTransactionDate(),
						transaction.getMerchantName(), transaction.getAmount(),
						rewardCalculator.calculatePoints(transaction.getAmount())))
				.toList();
	}

	private List<MonthlyRewardResponse> buildMonthlyRewards(List<PurchaseTransaction> transactions, LocalDate startDate,
			LocalDate endDate) {
		Map<YearMonth, List<PurchaseTransaction>> groupedByMonth = transactions.stream()
				.collect(Collectors.groupingBy(transaction -> YearMonth.from(transaction.getTransactionDate())));

		List<MonthlyRewardResponse> monthlyRewards = new ArrayList<>();
		YearMonth currentMonth = YearMonth.from(startDate);
		YearMonth endMonth = YearMonth.from(endDate);
		while (!currentMonth.isAfter(endMonth)) {
			monthlyRewards.add(toMonthlyReward(currentMonth, groupedByMonth.getOrDefault(currentMonth, List.of())));
			currentMonth = currentMonth.plusMonths(1);
		}
		return monthlyRewards;
	}

	private MonthlyRewardResponse toMonthlyReward(YearMonth month, List<PurchaseTransaction> monthTransactions) {
		BigDecimal totalSpend = monthTransactions.stream()
				.map(PurchaseTransaction::getAmount)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		int points = monthTransactions.stream()
				.mapToInt(transaction -> rewardCalculator.calculatePoints(transaction.getAmount()))
				.sum();
		String monthName = month.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
		return new MonthlyRewardResponse(month.getYear(), monthName, monthTransactions.size(), totalSpend, points);
	}
}
