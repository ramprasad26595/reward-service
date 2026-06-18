package com.reward.controller;

import com.reward.dto.CustomerResponse;
import com.reward.dto.RewardSummaryResponse;
import com.reward.service.RewardService;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@Validated
public class RewardController {

	private final RewardService rewardService;

	@Autowired
	public RewardController(RewardService rewardService) {
		this.rewardService = rewardService;
	}

	@GetMapping("/customers")
	public List<CustomerResponse> getCustomers() {
		return rewardService.getCustomers();
	}

	@GetMapping("/rewards")
	public RewardSummaryResponse calculateRewards(
			@RequestParam @NotNull Long customerId,
			@RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
		return rewardService.calculateRewards(customerId, startDate, endDate);
	}
}
