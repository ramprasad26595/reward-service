package com.reward.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class RewardCalculatorTest {

	private final RewardCalculator rewardCalculator = new RewardCalculator();

	@Test
	void calculatesNoPointsAtOrBelowFiftyDollars() {
		assertThat(rewardCalculator.calculatePoints(new BigDecimal("49.99"))).isZero();
		assertThat(rewardCalculator.calculatePoints(new BigDecimal("50.00"))).isZero();
	}

	@Test
	void calculatesOnePointForEveryWholeDollarBetweenFiftyAndOneHundred() {
		assertThat(rewardCalculator.calculatePoints(new BigDecimal("75.99"))).isEqualTo(25);
		assertThat(rewardCalculator.calculateBreakdown(new BigDecimal("99.99")).pointsBetweenFiftyAndOneHundred())
				.isEqualTo(49);
	}

	@Test
	void calculatesTwoPointsForEveryWholeDollarOverOneHundred() {
		assertThat(rewardCalculator.calculatePoints(new BigDecimal("120.00"))).isEqualTo(90);
		assertThat(rewardCalculator.calculateBreakdown(new BigDecimal("120.00")).dollarsOverOneHundred())
				.isEqualTo(20);
	}
}
