package com.reward.service;

import com.reward.dto.RewardBreakdownResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Component;

@Component
public class RewardCalculator {

	private static final int LOWER_THRESHOLD = 50;
	private static final int UPPER_THRESHOLD = 100;

	public int calculatePoints(BigDecimal amount) {
		RewardBreakdownResponse breakdown = calculateBreakdown(amount);
		return breakdown.pointsBetweenFiftyAndOneHundred() + breakdown.pointsOverOneHundred();
	}

	public RewardBreakdownResponse calculateBreakdown(BigDecimal amount) {
		int wholeDollars = amount.setScale(0, RoundingMode.DOWN).intValue();
		int dollarsBetweenFiftyAndOneHundred = Math.max(0, Math.min(wholeDollars, UPPER_THRESHOLD) - LOWER_THRESHOLD);
		int dollarsOverOneHundred = Math.max(0, wholeDollars - UPPER_THRESHOLD);
		return new RewardBreakdownResponse(dollarsBetweenFiftyAndOneHundred, dollarsOverOneHundred,
				dollarsBetweenFiftyAndOneHundred, dollarsOverOneHundred * 2);
	}
}
