package com.charter.reward.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.charter.reward.RewardServiceApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Verifies controller endpoints using the real application context and seeded H2 data.
 */
@SpringBootTest(classes = RewardServiceApplication.class)
@AutoConfigureMockMvc
class RewardControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void returnsSeededCustomersFromTheDatabase() throws Exception {
		mockMvc.perform(get("/api/v1/customers"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(3)))
				.andExpect(jsonPath("$[0].fullName").value("Aarav Sharma"))
				.andExpect(jsonPath("$[1].fullName").value("Priya Iyer"))
				.andExpect(jsonPath("$[2].fullName").value("Rohan Mehta"));
	}

	@Test
	void returnsRewardSummaryForTheSeededCustomer() throws Exception {
		mockMvc.perform(get("/api/v1/rewards")
						.param("customerId", "1")
						.param("startDate", "2026-04-01")
						.param("endDate", "2026-06-30"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.customerName").value("Aarav Sharma"))
				.andExpect(jsonPath("$.transactionCount").value(5))
				.andExpect(jsonPath("$.totalPoints").value(546))
				.andExpect(jsonPath("$.monthlyRewards", hasSize(3)))
				.andExpect(jsonPath("$.monthlyRewards[0].year").value(2026))
				.andExpect(jsonPath("$.monthlyRewards[0].month").value("April"))
				.andExpect(jsonPath("$.monthlyRewards[0].points").value(124))
				.andExpect(jsonPath("$.monthlyRewards[1].month").value("May"))
				.andExpect(jsonPath("$.monthlyRewards[1].points").value(274))
				.andExpect(jsonPath("$.monthlyRewards[2].month").value("June"))
				.andExpect(jsonPath("$.monthlyRewards[2].points").value(148));
	}

	@Test
	void returnsValidationErrorWhenEndDateIsMissing() throws Exception {
		mockMvc.perform(get("/api/v1/rewards")
						.param("customerId", "1")
						.param("startDate", "2026-04-01"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Validation failed"))
				.andExpect(jsonPath("$.details", hasSize(1)));
	}

	@Test
	void returnsMultipleValidationErrorsWhenSeveralFieldsAreInvalid() throws Exception {
		mockMvc.perform(get("/api/v1/rewards")
						.param("customerId", "")
						.param("startDate", "")
						.param("endDate", ""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Validation failed"))
				.andExpect(jsonPath("$.details", hasSize(3)))
				.andExpect(jsonPath("$.details[0]").exists())
				.andExpect(jsonPath("$.details[1]").exists())
				.andExpect(jsonPath("$.details[2]").exists());
	}
}
