package com.reward.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.reward.dto.CustomerResponse;
import com.reward.dto.RewardSummaryResponse;
import com.reward.service.RewardService;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@ExtendWith(MockitoExtension.class)
class RewardControllerTest {

	@Mock
	private RewardService rewardService;

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
		validator.afterPropertiesSet();
		mockMvc = MockMvcBuilders.standaloneSetup(new RewardController(rewardService))
				.setControllerAdvice(new com.reward.exception.GlobalExceptionHandler())
				.setValidator(validator)
				.build();
	}

	@Test
	void listsCustomers() throws Exception {
		when(rewardService.getCustomers()).thenReturn(List.of(
				new CustomerResponse(1L, "Aarav Sharma", "aarav.sharma@example.com"),
				new CustomerResponse(2L, "Priya Iyer", "priya.iyer@example.com")));

		mockMvc.perform(get("/api/v1/customers"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].fullName").value("Aarav Sharma"));
	}

	@Test
	void calculatesRewardsThroughRestApi() throws Exception {
		when(rewardService.calculateRewards(eq(1L), eq(LocalDate.parse("2026-04-01")),
				eq(LocalDate.parse("2026-06-30"))))
				.thenReturn(new RewardSummaryResponse(1L, "Aarav Sharma", "aarav.sharma@example.com",
						LocalDate.parse("2026-04-01"), LocalDate.parse("2026-06-30"), 5, 546,
						List.of(), Instant.parse("2026-06-17T00:00:00Z")));

		mockMvc.perform(get("/api/v1/rewards")
						.param("customerId", "1")
						.param("startDate", "2026-04-01")
						.param("endDate", "2026-06-30"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.customerName").value("Aarav Sharma"))
				.andExpect(jsonPath("$.totalPoints").value(546));
	}

	@Test
	void returnsNotFoundForUnknownCustomer() throws Exception {
		when(rewardService.calculateRewards(eq(99L), eq(LocalDate.parse("2026-04-01")),
				eq(LocalDate.parse("2026-06-30"))))
				.thenThrow(new com.reward.exception.ResourceNotFoundException("Customer 99 was not found"));

		mockMvc.perform(get("/api/v1/rewards")
						.param("customerId", "99")
						.param("startDate", "2026-04-01")
						.param("endDate", "2026-06-30"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Customer 99 was not found"));
	}
}
