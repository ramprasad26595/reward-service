package com.charter.reward.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class RewardCalculatorTest {

	private final RewardCalculator rewardCalculator = new RewardCalculator();

	@Test
	void calculatesNoPointsAtOrBelowFiftyDollars() {
		assertThat(rewardCalculator.calculatePoints(BigDecimal.ZERO)).isZero();
		assertThat(rewardCalculator.calculatePoints(new BigDecimal("49.99"))).isZero();
		assertThat(rewardCalculator.calculatePoints(new BigDecimal("50.00"))).isZero();
	}

	@Test
	void calculatesOnePointForEveryWholeDollarBetweenFiftyAndOneHundred() {
		assertThat(rewardCalculator.calculatePoints(new BigDecimal("75.99"))).isEqualTo(25);
	}

	@Test
	void calculatesTwoPointsForEveryWholeDollarOverOneHundred() {
		assertThat(rewardCalculator.calculatePoints(new BigDecimal("120.00"))).isEqualTo(90);
	}

	@Test
	void calculatesExactlyFiftyAsZeroPoints() {
		assertThat(rewardCalculator.calculatePoints(new BigDecimal("50.00"))).isZero();
	}

	@Test
	void calculatesExactlyOneHundredAsFiftyPoints() {
		assertThat(rewardCalculator.calculatePoints(new BigDecimal("100.00"))).isEqualTo(50);
	}

	@Test
	void rejectsNegativeAmounts() {
		assertThatThrownBy(() -> rewardCalculator.calculatePoints(new BigDecimal("-1.00")))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("amount must be zero or positive");
	}
}
