package com.nadoyagsa.pillaroid.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AlarmTimeDto {
	private long alarmTimeIdx;
	private String alarmToken;
	private String medicineName;
	private String amount;
}
