package com.nadoyagsa.pillaroid.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nadoyagsa.pillaroid.entity.Notification;
import com.nadoyagsa.pillaroid.entity.NotificationTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class NotificationTimeResponse {
	private long notificationIdx;
	private String name;
	private long period;
	private String dosage;
	private List<NotificationTimeDto> notificationTimes;

	public NotificationTimeResponse(Notification notification, List<NotificationTime> notificationTimes) {
		this.notificationIdx = notification.getNotificationIdx();
		this.name = notification.getName();
		this.period = notification.getPeriod();
		this.dosage = notification.getDosage();
		this.notificationTimes = notificationTimes.stream()
				.map(NotificationTime::toNotificationTimeDto)
				.collect(Collectors.toList());
	}
}
