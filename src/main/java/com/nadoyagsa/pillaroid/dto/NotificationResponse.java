package com.nadoyagsa.pillaroid.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationResponse {
	private long notificationIdx;
	private String name;
	private int period;
	private String dosage;
}
