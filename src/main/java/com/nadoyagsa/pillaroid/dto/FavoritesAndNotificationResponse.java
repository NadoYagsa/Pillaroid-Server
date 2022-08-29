package com.nadoyagsa.pillaroid.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FavoritesAndNotificationResponse {
	private Long favoritesIdx = null;
	private NotificationResponse notificationResponse = null;
}
