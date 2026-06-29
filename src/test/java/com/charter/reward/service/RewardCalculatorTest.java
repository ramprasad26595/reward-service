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
	void ignoresFractionalDollarsWhenCalculatingPoints() {
		// $84.25 -> 34 whole dollars over $50 -> 34 points (cents ignored)
		assertThat(rewardCalculator.calculatePoints(new BigDecimal("84.25"))).isEqualTo(34);
		// $100.99 -> 50 points for the $50-$100 band, cents above $100 ignored
		assertThat(rewardCalculator.calculatePoints(new BigDecimal("100.99"))).isEqualTo(50);
	}

	@Test
	void calculatesPointsJustBelowAndAboveTheUpperThreshold() {
		assertThat(rewardCalculator.calculatePoints(new BigDecimal("99.00"))).isEqualTo(49);
		assertThat(rewardCalculator.calculatePoints(new BigDecimal("101.00"))).isEqualTo(52);
	}

	@Test
	void calculatesPointsForVeryLargeAmounts() {
		// 50 points for $50-$100 band plus 2 * (1_000_000 - 100)
		assertThat(rewardCalculator.calculatePoints(new BigDecimal("1000000.00"))).isEqualTo(50 + (2 * 999_900));
	}

	@Test
	void rejectsNegativeAmounts() {
		assertThatThrownBy(() -> rewardCalculator.calculatePoints(new BigDecimal("-1.00")))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("amount must be zero or positive");
	}

	@Test
	void rejectsNullAmount() {
		assertThatThrownBy(() -> rewardCalculator.calculatePoints(null))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("amount must not be null");
	}
}
