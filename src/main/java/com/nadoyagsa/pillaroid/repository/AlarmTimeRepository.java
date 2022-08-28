package com.nadoyagsa.pillaroid.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nadoyagsa.pillaroid.entity.AlarmTime;

public interface AlarmTimeRepository extends JpaRepository<AlarmTime, Long> {
}
