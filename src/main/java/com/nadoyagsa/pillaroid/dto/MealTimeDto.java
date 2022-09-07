package com.nadoyagsa.pillaroid.dto;

import java.time.LocalTime;

import javax.validation.constraints.NotNull;

import com.nadoyagsa.pillaroid.entity.MealTime;
import com.nadoyagsa.pillaroid.entity.User;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MealTimeDto {
	@NotNull(message = "morning 필수")
	private LocalTime morning;
	@NotNull(message = "lunch 필수")
	private LocalTime lunch;
	@NotNull(message = "dinner 필수")
	private LocalTime dinner;

	public MealTime toEntity(User user) {
		return MealTime.builder()
				.user(user)
				.morning(this.morning)
				.lunch(this.lunch)
				.dinner(this.dinner)
				.build();

	}
}
