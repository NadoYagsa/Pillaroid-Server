package com.nadoyagsa.pillaroid.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.nadoyagsa.pillaroid.dto.AlarmResponse;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Table(name = "alarm")
@Entity
public class Alarm {
	@Id
	@Column(name = "alarm_idx")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long alarmIdx;

	@ManyToOne
	@JoinColumn(name = "user_idx")
	private User user;

	@ManyToOne
	@JoinColumn(name = "medicine_idx")
	private Medicine medicine;

	@Column(name = "name")
	private String name;

	@Column
	private int period;

	@Column
	private String amount;

	@Column
	private String dosage;

	public AlarmResponse toAlarmResponse() {
		return AlarmResponse.builder()
				.alarmIdx(this.alarmIdx)
				.medicineIdx(this.medicine.getMedicineIdx())
				.name(this.name)
				.period(this.period)
				.dosage(this.dosage)
				.build();
	}

}
