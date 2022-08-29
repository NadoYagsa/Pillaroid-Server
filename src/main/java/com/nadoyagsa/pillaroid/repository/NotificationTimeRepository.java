package com.nadoyagsa.pillaroid.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nadoyagsa.pillaroid.entity.Notification;
import com.nadoyagsa.pillaroid.entity.NotificationTime;

public interface NotificationTimeRepository extends JpaRepository<NotificationTime, Long> {
	List<NotificationTime> findByNotification(Notification notification);
}
