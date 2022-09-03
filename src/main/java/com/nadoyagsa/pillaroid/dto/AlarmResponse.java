package com.nadoyagsa.pillaroid.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlarmResponse {
	private long alarmIdx;
	private int medicineIdx;
	private String name;
	private int period;
	private String dosage;
}
