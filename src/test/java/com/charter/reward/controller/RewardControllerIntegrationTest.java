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
 * Verifies controller endpoints using the real application context and seeded H2 data,
 * including API-level negative and boundary scenarios.
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
				.andExpect(jsonPath("$.customerDetails.fullName").value("Aarav Sharma"))
				.andExpect(jsonPath("$.transactionCount").value(5))
				.andExpect(jsonPath("$.transactionDetails", hasSize(5)))
				.andExpect(jsonPath("$.transactionDetails[0].merchantName").value("Reliance Fresh"))
				.andExpect(jsonPath("$.transactionDetails[0].points").value(34))
				.andExpect(jsonPath("$.totalRewardPoints").value(546))
				.andExpect(jsonPath("$.monthlyRewardPoints", hasSize(3)))
				.andExpect(jsonPath("$.monthlyRewardPoints[0].year").value(2026))
				.andExpect(jsonPath("$.monthlyRewardPoints[0].month").value("April"))
				.andExpect(jsonPath("$.monthlyRewardPoints[0].points").value(124))
				.andExpect(jsonPath("$.monthlyRewardPoints[1].month").value("May"))
				.andExpect(jsonPath("$.monthlyRewardPoints[1].points").value(274))
				.andExpect(jsonPath("$.monthlyRewardPoints[2].month").value("June"))
				.andExpect(jsonPath("$.monthlyRewardPoints[2].points").value(148));
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
	void returnsBadRequestForInvalidDateFormat() throws Exception {
		mockMvc.perform(get("/api/v1/rewards")
						.param("customerId", "1")
						.param("startDate", "2026-13-45")
						.param("endDate", "2026-06-30"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.message").value("Validation failed"));
	}

	@Test
	void returnsBadRequestForNonNumericCustomerId() throws Exception {
		mockMvc.perform(get("/api/v1/rewards")
						.param("customerId", "abc")
						.param("startDate", "2026-04-01")
						.param("endDate", "2026-06-30"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Validation failed"));
	}

	@Test
	void returnsBadRequestWhenEndDateIsBeforeStartDate() throws Exception {
		mockMvc.perform(get("/api/v1/rewards")
						.param("customerId", "1")
						.param("startDate", "2026-06-30")
						.param("endDate", "2026-04-01"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("endDate must be on or after startDate"));
	}

	@Test
	void returnsNotFoundForUnknownCustomer() throws Exception {
		mockMvc.perform(get("/api/v1/rewards")
						.param("customerId", "999")
						.param("startDate", "2026-04-01")
						.param("endDate", "2026-06-30"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.message").value("Customer 999 was not found"));
	}

	@Test
	void returnsEmptyBreakdownForRangeWithNoTransactions() throws Exception {
		mockMvc.perform(get("/api/v1/rewards")
						.param("customerId", "1")
						.param("startDate", "2020-01-01")
						.param("endDate", "2020-01-31"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.transactionCount").value(0))
				.andExpect(jsonPath("$.transactionDetails", hasSize(0)))
				.andExpect(jsonPath("$.totalRewardPoints").value(0));
	}
}
