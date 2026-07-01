package com.charter.reward.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.charter.reward.dto.CustomerResponse;
import com.charter.reward.dto.MonthlyRewardResponse;
import com.charter.reward.dto.RewardSummaryResponse;
import com.charter.reward.dto.TransactionDetailResponse;
import com.charter.reward.service.RewardService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(RewardController.class)
class RewardControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private RewardService rewardService;

	@Test
	void getCustomersReturnsListFromService() throws Exception {
		when(rewardService.getCustomers()).thenReturn(List.of(
				new CustomerResponse(1L, "Aarav Sharma", "aarav@example.com"),
				new CustomerResponse(2L, "Priya Iyer", "priya@example.com")));

		mockMvc.perform(get("/api/v1/customers"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(2))
				.andExpect(jsonPath("$[0].fullName").value("Aarav Sharma"));
	}

	@Test
	void calculateRewardsReturnsRewardSummary() throws Exception {
		LocalDate start = LocalDate.parse("2026-04-01");
		LocalDate end = LocalDate.parse("2026-04-30");

		RewardSummaryResponse summary = new RewardSummaryResponse(
				new CustomerResponse(1L, "Aarav Sharma", "aarav@example.com"),
				start, end, 1,
				List.of(new TransactionDetailResponse(1L, LocalDate.parse("2026-04-03"), "Store",
						new BigDecimal("84.25"), 34)),
				List.of(new MonthlyRewardResponse(2026, "April", 1, new BigDecimal("84.25"), 34)),
				34);

		when(rewardService.calculateRewards(1L, start, end)).thenReturn(summary);

		mockMvc.perform(get("/api/v1/rewards")
						.param("customerId", "1")
						.param("startDate", "2026-04-01")
						.param("endDate", "2026-04-30"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.customerDetails.fullName").value("Aarav Sharma"))
				.andExpect(jsonPath("$.transactionCount").value(1))
				.andExpect(jsonPath("$.totalRewardPoints").value(34));
	}
}
