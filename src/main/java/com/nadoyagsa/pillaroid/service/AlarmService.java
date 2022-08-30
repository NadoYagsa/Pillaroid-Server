package com.nadoyagsa.pillaroid.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nadoyagsa.pillaroid.common.exception.BadRequestException;
import com.nadoyagsa.pillaroid.common.exception.ForbiddenException;
import com.nadoyagsa.pillaroid.common.exception.NotFoundException;
import com.nadoyagsa.pillaroid.dto.AlarmResponse;
import com.nadoyagsa.pillaroid.dto.AlarmTimeDto;
import com.nadoyagsa.pillaroid.dto.AlarmTimeResponse;
import com.nadoyagsa.pillaroid.entity.Medicine;
import com.nadoyagsa.pillaroid.entity.Alarm;
import com.nadoyagsa.pillaroid.entity.AlarmTime;
import com.nadoyagsa.pillaroid.entity.User;
import com.nadoyagsa.pillaroid.repository.MealTimeRepository;
import com.nadoyagsa.pillaroid.repository.MedicineRepository;
import com.nadoyagsa.pillaroid.repository.AlarmRepository;
import com.nadoyagsa.pillaroid.repository.AlarmTimeRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AlarmService {

	private final AlarmRepository alarmRepository;
	private final AlarmTimeRepository alarmTimeRepository;
	private final MedicineRepository medicineRepository;
	private final MealTimeRepository mealTimeRepository;

	// 의약품에 해당하는 사용자 알림 조회
	public AlarmResponse findAlarmByUserAndMedicineIdx(User user, int medicineIdx) {
		Medicine medicine = medicineRepository.findById(medicineIdx)
				.orElseThrow(() -> BadRequestException.BAD_PARAMETER);
		Alarm alarm = alarmRepository.findByUserAndMedicine(user, medicine)
				.orElseThrow(() -> NotFoundException.DATA_NOT_FOUND);
		return alarm.toAlarmResponse();
	}

	// 사용자 관련 알림 목록 조회
	public List<AlarmResponse> findAlarmByUser(User user) {
		List<Alarm> alarms = alarmRepository.findByUser(user);
		return alarms.stream()
				.map(Alarm::toAlarmResponse)
				.collect(Collectors.toList());
	}

	// 의약품에 해당하는 사용자 알림 삭제
	@Transactional
	public AlarmTimeResponse deleteAlarm(User user, long alarmIdx) throws NotFoundException, ForbiddenException {
		// 알림 데이터가 있는지 검사
		Alarm alarm = alarmRepository.findById(alarmIdx)
				.orElseThrow(() -> BadRequestException.BAD_PARAMETER);

		// 사용자 본인이 데이터를 지우는 것인지 검사
		if (!Objects.equals(alarm.getUser().getUserIdx(), user.getUserIdx())) {
			throw ForbiddenException.deleteForbidden;
		}

		List<AlarmTime> alarmTimes = alarmTimeRepository.findByAlarm(alarm);		// 삭제할 알림과 관련된 시간 데이터 조회
		alarmRepository.deleteById(alarm.getAlarmIdx());	// 데이터 삭제

		// 전달할 Response 생성
		List<AlarmTimeDto> alarmTimeDtos = alarmTimes.stream()
				.map(AlarmTime::toAlarmTimeDto)
				.collect(Collectors.toList());

		return AlarmTimeResponse.builder()
				.alarmIdx(alarmIdx)
				.alarmTimeList(alarmTimeDtos)
				.build();
	}
}
