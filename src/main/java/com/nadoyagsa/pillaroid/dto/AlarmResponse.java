package com.nadoyagsa.pillaroid.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AlarmResponse {
	private long alarmIdx;
	private int medicineIdx;
	private String name;
	private int period;
	private String dosage;
}
