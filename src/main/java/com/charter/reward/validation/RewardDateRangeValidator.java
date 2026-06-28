package com.charter.reward.validation;

import com.charter.reward.exception.BadRequestException;
import java.time.LocalDate;
import org.springframework.stereotype.Component;

/**
 * Validates reward request date ranges.
 */
@Component
public class RewardDateRangeValidator {

	/**
	 * Validates the supplied date range.
	 *
	 * @param startDate start of range
	 * @param endDate end of range
	 */
	public void validate(LocalDate startDate, LocalDate endDate) {
		if (startDate == null || endDate == null) {
			throw new BadRequestException("startDate and endDate are required");
		}
		if (endDate.isBefore(startDate)) {
			throw new BadRequestException("endDate must be on or after startDate");
		}
	}
}
