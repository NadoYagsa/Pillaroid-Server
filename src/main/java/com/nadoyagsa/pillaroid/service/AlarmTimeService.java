package com.nadoyagsa.pillaroid.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nadoyagsa.pillaroid.dto.AlarmTimeDto;
import com.nadoyagsa.pillaroid.entity.AlarmTime;
import com.nadoyagsa.pillaroid.entity.User;
import com.nadoyagsa.pillaroid.repository.AlarmTimeRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AlarmTimeService {

	private final AlarmTimeRepository alarmTimeRepository;

	// 알림 시간대
	public Optional<AlarmTime> findAlarmTimeByUserIdx(Long userIdx) {
		return alarmTimeRepository.findById(userIdx);
	}

	// 알림 시간대 저장
	@Transactional
	public AlarmTime saveAlarmTime(User user, AlarmTimeDto alarmTimeDto) {
		// 사용자의 알림 시간대 조회
		Optional<AlarmTime> optAlarmTime = alarmTimeRepository.findById(user.getUserIdx());

		AlarmTime alarmTime;
		if (optAlarmTime.isPresent()) {		// 조회 O
			alarmTime = optAlarmTime.get();
			alarmTime.updateAlarmTime(alarmTimeDto);	// 알림 시간대 업데이트
		} else {							// 조회 X
			alarmTime = alarmTimeDto.toEntity(user);	// 알림 시간대 생성
		}

		alarmTimeRepository.save(alarmTime);

		return alarmTime;
	}
}
