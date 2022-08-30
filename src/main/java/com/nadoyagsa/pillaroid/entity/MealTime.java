package com.nadoyagsa.pillaroid.entity;

import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.nadoyagsa.pillaroid.dto.MealTimeDto;
import com.nadoyagsa.pillaroid.dto.MealTimeResponse;

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
@Table(name = "meal_time")
public class MealTime {
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

	public void updateMealTime(MealTimeDto mealTimeDto) {
		this.morning = mealTimeDto.getMorning();
		this.lunch = mealTimeDto.getLunch();
		this.dinner = mealTimeDto.getDinner();
	}

	public MealTimeResponse toMealTimeResponse() {
		return MealTimeResponse.builder()
				.userIdx(this.userIdx)
				.morning(this.morning)
				.lunch(this.lunch)
				.dinner(this.dinner)
				.build();
	}
}
