package com.nadoyagsa.pillaroid.dto;

import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationDto {
	private long userIdx;

	@NotNull(message = "medicineIdx 필수")
	private int medicineIdx;

	@NotNull(message = "name 필수")
	private String name;

	@NotNull(message = "period 필수")
	private int period;

	private String dosage;
}
