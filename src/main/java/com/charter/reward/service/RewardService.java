package com.charter.reward.service;

import com.charter.reward.dto.CustomerResponse;
import com.charter.reward.dto.RewardSummaryResponse;
import java.time.LocalDate;
import java.util.List;

/**
 * Reward calculation use cases.
 */
public interface RewardService {

	List<CustomerResponse> getCustomers();

	RewardSummaryResponse calculateRewards(Long customerId, LocalDate startDate, LocalDate endDate);
}
