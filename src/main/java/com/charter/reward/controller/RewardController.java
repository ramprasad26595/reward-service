package com.charter.reward.controller;

import com.charter.reward.dto.CustomerResponse;
import com.charter.reward.dto.RewardSummaryResponse;
import com.charter.reward.service.RewardService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exposes reward calculation endpoints.
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RewardController {

	private final RewardService rewardService;

	@GetMapping("/customers")
	public List<CustomerResponse> getCustomers() {
		return rewardService.getCustomers();
	}

	@GetMapping("/rewards")
	public RewardSummaryResponse calculateRewards(
			@RequestParam Long customerId,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
		return rewardService.calculateRewards(customerId, startDate, endDate);
	}
}
