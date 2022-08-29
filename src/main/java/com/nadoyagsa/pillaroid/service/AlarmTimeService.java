package com.nadoyagsa.pillaroid.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nadoyagsa.pillaroid.common.exception.NotFoundException;
import com.nadoyagsa.pillaroid.dto.AlarmTimeDto;
import com.nadoyagsa.pillaroid.dto.AlarmTimeResponse;
import com.nadoyagsa.pillaroid.entity.AlarmTime;
import com.nadoyagsa.pillaroid.entity.User;
import com.nadoyagsa.pillaroid.repository.AlarmTimeRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AlarmTimeService {

	private final AlarmTimeRepository alarmTimeRepository;

	// 복용 시간대 조회
	public AlarmTimeResponse findAlarmTimeByUser(User user) {
		long userIdx = user.getUserIdx();
		AlarmTime alarmTime = alarmTimeRepository.findById(userIdx)
				.orElseThrow(() -> NotFoundException.DATA_NOT_FOUND);
		return alarmTime.toAlarmTimeResponse();
	}

	// 복용 시간대 저장
	@Transactional
	public AlarmTimeResponse saveAlarmTime(User user, AlarmTimeDto alarmTimeDto) {
		// 사용자의 복용 시간대 조회
		Optional<AlarmTime> optAlarmTime = alarmTimeRepository.findById(user.getUserIdx());

		AlarmTime alarmTime;
		if (optAlarmTime.isPresent()) {		// 조회 O
			alarmTime = optAlarmTime.get();
			alarmTime.updateAlarmTime(alarmTimeDto);	// 복용 시간대 업데이트
		} else {							// 조회 X
			alarmTime = alarmTimeDto.toEntity(user);	// 복용 시간대 생성
		}

		alarmTimeRepository.save(alarmTime);

		return alarmTime.toAlarmTimeResponse();
	}
}
