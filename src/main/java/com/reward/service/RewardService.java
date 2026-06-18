package com.reward.service;

import com.reward.dto.CustomerResponse;
import com.reward.dto.RewardSummaryResponse;
import java.time.LocalDate;
import java.util.List;

public interface RewardService {

	List<CustomerResponse> getCustomers();

	RewardSummaryResponse calculateRewards(Long customerId, LocalDate startDate, LocalDate endDate);
}
