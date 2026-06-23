package com.charter.reward.controller;

import com.charter.reward.dto.CustomerResponse;
import com.charter.reward.dto.RewardQueryRequest;
import com.charter.reward.dto.RewardSummaryResponse;
import com.charter.reward.service.RewardService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exposes reward calculation endpoints.
 */
@RestController
@RequestMapping("/api/v1")
@Validated
@RequiredArgsConstructor
public class RewardController {

	private final RewardService rewardService;

	@GetMapping("/customers")
	public List<CustomerResponse> getCustomers() {
		return rewardService.getCustomers();
	}

	@GetMapping("/rewards")
	public RewardSummaryResponse calculateRewards(@ModelAttribute @Valid RewardQueryRequest request) {
		return rewardService.calculateRewards(Long.valueOf(request.customerId()), parseDate(request.startDate()),
				parseDate(request.endDate()));
	}

	private LocalDate parseDate(String value) {
		return LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE);
	}
}
