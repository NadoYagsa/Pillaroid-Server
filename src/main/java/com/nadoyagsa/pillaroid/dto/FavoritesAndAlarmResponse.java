package com.nadoyagsa.pillaroid.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FavoritesAndAlarmResponse {
	private Long favoritesIdx;
	private AlarmResponse alarmResponse;
}
