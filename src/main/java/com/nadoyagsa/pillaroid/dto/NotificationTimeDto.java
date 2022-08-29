package com.nadoyagsa.pillaroid.dto;

import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationTimeDto {
	private long notificationTimeIdx;
	private LocalTime time;
}
