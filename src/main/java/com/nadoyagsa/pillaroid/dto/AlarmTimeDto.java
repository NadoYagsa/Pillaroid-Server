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
	@NotNull
	private LocalTime morning;
	@NotNull
	private LocalTime lunch;
	@NotNull
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
