package com.nadoyagsa.pillaroid.repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nadoyagsa.pillaroid.entity.AlarmTime;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AlarmTimeRepository extends JpaRepository<AlarmTime, Long> {
	@Query("SELECT a FROM AlarmTime a WHERE a.time = :time")
	List<AlarmTime> findByAlarmTime(@Param("time") LocalDateTime time);
}
