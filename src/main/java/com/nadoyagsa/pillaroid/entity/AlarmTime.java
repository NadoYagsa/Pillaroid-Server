package com.nadoyagsa.pillaroid.entity;

import java.time.LocalDateTime;

import javax.persistence.*;

import com.nadoyagsa.pillaroid.dto.AlarmTimeDto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Table(name = "alarm_time")
@Entity
public class AlarmTime {
	@Id
	@Column(name = "alarm_time_idx")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long alarmTimeIdx;

	@ManyToOne
	@JoinColumn(name = "alarm_idx")
	private Alarm alarm;

	@Getter
	@Column
	private LocalDateTime time;

	public AlarmTimeDto toAlarmTimeDto() {
		// 의약품 이름에서 괄호 이후는 제거함
		int index = alarm.getMedicine().getName().indexOf("(");
		String medicineName = index>-1 ? alarm.getMedicine().getName().substring(0, index) : alarm.getMedicine().getName();

		return AlarmTimeDto.builder()
				.alarmTimeIdx(this.alarmTimeIdx)
				.alarmToken(alarm.getUser().getAlarmToken())
				.medicineName(medicineName.strip())
				.amount(alarm.getAmount())
				.build();
	}
}
