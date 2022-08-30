package com.nadoyagsa.pillaroid.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nadoyagsa.pillaroid.entity.Alarm;
import com.nadoyagsa.pillaroid.entity.AlarmTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class AlarmTimeResponse {
	private long alarmIdx;
	private int medicineIdx;
	private String name;
	private long period;
	private String dosage;
	private List<AlarmTimeDto> alarmTimeList;

	public AlarmTimeResponse(Alarm alarm, List<AlarmTime> alarmTimes) {
		this.alarmIdx = alarm.getAlarmIdx();
		this.medicineIdx = alarm.getMedicine().getMedicineIdx();
		this.name = alarm.getName();
		this.period = alarm.getPeriod();
		this.dosage = alarm.getDosage();
		this.alarmTimeList = alarmTimes.stream()
				.map(AlarmTime::toAlarmTimeDto)
				.collect(Collectors.toList());
	}
}
