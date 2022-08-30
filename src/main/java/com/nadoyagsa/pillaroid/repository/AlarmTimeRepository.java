package com.nadoyagsa.pillaroid.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nadoyagsa.pillaroid.entity.Alarm;
import com.nadoyagsa.pillaroid.entity.AlarmTime;

public interface AlarmTimeRepository extends JpaRepository<AlarmTime, Long> {
	List<AlarmTime> findByAlarm(Alarm alarm);
}
