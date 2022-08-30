package com.nadoyagsa.pillaroid.dto;

import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class MealTimeResponse {
	private long userIdx;		// 사용자 번호
	private LocalTime morning;	// 아침 식사 시간
	private LocalTime lunch;	// 점심 식사 시간
	private LocalTime dinner;	// 저녁 식사 시간
}
