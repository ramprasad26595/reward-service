package com.reward.service;

import com.reward.dto.CustomerResponse;
import com.reward.dto.MonthlyRewardResponse;
import com.reward.dto.RewardSummaryResponse;
import com.reward.dto.TransactionRewardResponse;
import com.reward.entity.Customer;
import com.reward.entity.PurchaseTransaction;
import com.reward.exception.BadRequestException;
import com.reward.exception.ResourceNotFoundException;
import com.reward.repository.CustomerRepository;
import com.reward.repository.PurchaseTransactionRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RewardServiceImpl implements RewardService {

	private final CustomerRepository customerRepository;
	private final PurchaseTransactionRepository transactionRepository;
	private final RewardCalculator rewardCalculator;

	@Autowired
	public RewardServiceImpl(CustomerRepository customerRepository, PurchaseTransactionRepository transactionRepository,
			RewardCalculator rewardCalculator) {
		this.customerRepository = customerRepository;
		this.transactionRepository = transactionRepository;
		this.rewardCalculator = rewardCalculator;
	}

	@Override
	public List<CustomerResponse> getCustomers() {
		return customerRepository.findAll().stream()
				.sorted(Comparator.comparing(Customer::getFullName))
				.map(this::toCustomerResponse)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public RewardSummaryResponse calculateRewards(Long customerId, LocalDate startDate, LocalDate endDate) {
		validateDateRange(startDate, endDate);
		Customer customer = getCustomerOrThrow(customerId);
		List<PurchaseTransaction> transactions = transactionRepository
				.findByCustomerIdAndTransactionDateBetweenOrderByTransactionDateAsc(customerId, startDate, endDate);

		List<MonthlyRewardResponse> monthlyRewards = buildMonthlyRewards(transactions, startDate, endDate);
		int totalPoints = monthlyRewards.stream().mapToInt(MonthlyRewardResponse::points).sum();

		return new RewardSummaryResponse(customer.getId(), customer.getFullName(), customer.getEmail(),
				startDate, endDate, transactions.size(), totalPoints, monthlyRewards, Instant.now());
	}

	private List<MonthlyRewardResponse> buildMonthlyRewards(List<PurchaseTransaction> transactions,
			LocalDate startDate, LocalDate endDate) {
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

	private MonthlyRewardResponse toMonthlyReward(YearMonth month, List<PurchaseTransaction> transactions) {
		List<TransactionRewardResponse> transactionResponses = transactions.stream()
				.sorted(Comparator.comparing(PurchaseTransaction::getTransactionDate))
				.map(this::toTransactionRewardResponse)
				.toList();
		BigDecimal totalSpend = transactions.stream()
				.map(PurchaseTransaction::getAmount)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		int points = transactionResponses.stream().mapToInt(TransactionRewardResponse::points).sum();
		return new MonthlyRewardResponse(month.toString(), transactions.size(), totalSpend, points, transactionResponses);
	}

	private TransactionRewardResponse toTransactionRewardResponse(PurchaseTransaction transaction) {
		return new TransactionRewardResponse(transaction.getId(), transaction.getTransactionDate(), transaction.getAmount(),
				transaction.getMerchantName(), rewardCalculator.calculatePoints(transaction.getAmount()),
				rewardCalculator.calculateBreakdown(transaction.getAmount()));
	}

	private CustomerResponse toCustomerResponse(Customer customer) {
		return new CustomerResponse(customer.getId(), customer.getFullName(), customer.getEmail());
	}

	private Customer getCustomerOrThrow(Long customerId) {
		return customerRepository.findById(customerId)
				.orElseThrow(() -> new ResourceNotFoundException("Customer " + customerId + " was not found"));
	}

	private void validateDateRange(LocalDate startDate, LocalDate endDate) {
		if (endDate.isBefore(startDate)) {
			throw new BadRequestException("endDate must be on or after startDate");
		}
	}
}
