package com.nadoyagsa.pillaroid.dto;

import java.time.LocalTime;

import javax.validation.constraints.NotNull;

import com.nadoyagsa.pillaroid.entity.AlarmTime;
import com.nadoyagsa.pillaroid.entity.User;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AlarmTimeDto {
	@NotNull(message = "morning 필수")
	private LocalTime morning;
	@NotNull(message = "lunch 필수")
	private LocalTime lunch;
	@NotNull(message = "dinner 필수")
	private LocalTime dinner;

	public AlarmTime toEntity(User user) {
		return AlarmTime.builder()
				.user(user)
				.morning(this.morning)
				.lunch(this.lunch)
				.dinner(this.dinner)
				.build();

	}
}
