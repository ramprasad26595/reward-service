package com.charter.reward.validation;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.charter.reward.exception.BadRequestException;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class RewardDateRangeValidatorTest {

	private final RewardDateRangeValidator validator = new RewardDateRangeValidator();

	@Test
	void acceptsValidRange() {
		assertThatCode(() -> validator.validate(LocalDate.parse("2026-04-01"), LocalDate.parse("2026-06-30")))
				.doesNotThrowAnyException();
	}

	@Test
	void acceptsSameDayRange() {
		assertThatCode(() -> validator.validate(LocalDate.parse("2026-04-01"), LocalDate.parse("2026-04-01")))
				.doesNotThrowAnyException();
	}

	@Test
	void rejectsNullStartDate() {
		assertThatThrownBy(() -> validator.validate(null, LocalDate.parse("2026-06-30")))
				.isInstanceOf(BadRequestException.class)
				.hasMessage("startDate and endDate are required");
	}

	@Test
	void rejectsNullEndDate() {
		assertThatThrownBy(() -> validator.validate(LocalDate.parse("2026-04-01"), null))
				.isInstanceOf(BadRequestException.class)
				.hasMessage("startDate and endDate are required");
	}

	@Test
	void rejectsEndDateBeforeStartDate() {
		assertThatThrownBy(() -> validator.validate(LocalDate.parse("2026-06-30"), LocalDate.parse("2026-04-01")))
				.isInstanceOf(BadRequestException.class)
				.hasMessage("endDate must be on or after startDate");
	}
}
