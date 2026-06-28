package com.charter.reward.validation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Component;

/**
 * Calculates reward points from a purchase amount.
 */
@Component
public class RewardCalculator {

	private static final int LOWER_THRESHOLD = 50;
	private static final int UPPER_THRESHOLD = 100;

	/**
	 * Calculates reward points for the supplied amount.
	 *
	 * @param amount purchase amount
	 * @return reward points
	 */
	public int calculatePoints(BigDecimal amount) {
		if (amount == null) {
			throw new IllegalArgumentException("amount must not be null");
		}
		if (amount.signum() < 0) {
			throw new IllegalArgumentException("amount must be zero or positive");
		}
		int wholeDollars = amount.setScale(0, RoundingMode.DOWN).intValue();
		int dollarsBetweenFiftyAndOneHundred = Math.max(0, Math.min(wholeDollars, UPPER_THRESHOLD) - LOWER_THRESHOLD);
		int dollarsOverOneHundred = Math.max(0, wholeDollars - UPPER_THRESHOLD);
		return dollarsBetweenFiftyAndOneHundred + (dollarsOverOneHundred * 2);
	}
}
