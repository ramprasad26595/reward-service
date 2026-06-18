package com.reward.dto;

public record RewardBreakdownResponse(
		int dollarsBetweenFiftyAndOneHundred,
		int dollarsOverOneHundred,
		int pointsBetweenFiftyAndOneHundred,
		int pointsOverOneHundred) {
}
