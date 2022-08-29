package com.nadoyagsa.pillaroid.entity;

import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.nadoyagsa.pillaroid.dto.NotificationTimeDto;
import com.nadoyagsa.pillaroid.dto.NotificationTimeResponse;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Table(name = "notification_time")
@Entity
public class NotificationTime {
	@Id
	@Column(name = "notification_time_idx")
	private Long notificationTimeIdx;

	@OneToOne
	@JoinColumn(name = "notification_idx")
	private Notification notification;

	@Column
	private LocalTime time;

	public NotificationTimeDto toNotificationTimeDto() {
		return NotificationTimeDto.builder()
				.notificationTimeIdx(this.notificationTimeIdx)
				.time(this.time)
				.build();
	}
}
