package com.nadoyagsa.pillaroid.entity;

import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.nadoyagsa.pillaroid.dto.AlarmTimeDto;
import com.nadoyagsa.pillaroid.dto.AlarmTimeResponse;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
@Table(name = "alarm_time")
public class AlarmTime {
	@Id
	@Column(name = "user_idx")
	private Long userIdx;

	@OneToOne
	@MapsId
	@JoinColumn(name = "user_idx", referencedColumnName = "user_idx")
	private User user;

	@Column(nullable = false)
	private LocalTime morning;

	@Column(nullable = false)
	private LocalTime lunch;

	@Column(nullable = false)
	private LocalTime dinner;

	public void updateAlarmTime(AlarmTimeDto alarmTimeDto) {
		this.morning = alarmTimeDto.getMorning();
		this.lunch = alarmTimeDto.getLunch();
		this.dinner = alarmTimeDto.getDinner();
	}

	public AlarmTimeResponse toAlarmTimeResponse() {
		return AlarmTimeResponse.builder()
				.userIdx(this.userIdx)
				.morning(this.morning)
				.lunch(this.lunch)
				.dinner(this.dinner)
				.build();
	}
}
