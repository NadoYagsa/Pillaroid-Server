package com.nadoyagsa.pillaroid.service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nadoyagsa.pillaroid.common.exception.BadRequestException;
import com.nadoyagsa.pillaroid.common.exception.ForbiddenException;
import com.nadoyagsa.pillaroid.common.exception.NotFoundException;
import com.nadoyagsa.pillaroid.dto.AlarmDto;
import com.nadoyagsa.pillaroid.dto.AlarmResponse;
import com.nadoyagsa.pillaroid.dto.AlarmTimeDto;
import com.nadoyagsa.pillaroid.dto.AlarmTimeResponse;
import com.nadoyagsa.pillaroid.entity.MealTime;
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

	// 의약품에 해당하는 사용자 알림 등록
	@Transactional
	public AlarmTimeResponse saveAlarm(User user, AlarmDto alarmDto) {
		Medicine medicine = medicineRepository.findById(alarmDto.getMedicineIdx())
				.orElseThrow(() -> BadRequestException.BAD_PARAMETER);

		Map<String, String> dosageSummary = parseDosage(medicine.getDosage());	// TODO: 메소드 구현
		Integer[] threeTakingTime = getThreeTakingTime(dosageSummary.get("number"), dosageSummary.get("when"));	// TODO: 메소드 구현

		// 알림 저장
		Alarm alarm = Alarm.builder()
				.user(user)
				.medicine(medicine)
				.name(alarmDto.getName())
				.period(alarmDto.getPeriod())
				.dosage(String.format("%s, %s, %s", dosageSummary.get("number"), dosageSummary.get("amount"), dosageSummary.get("when")))
				.build();
		Alarm savedAlarm = alarmRepository.save(alarm);

		// 알림 시간 저장
		List<AlarmTime> savedAlarmTimes = alarmTimeRepository.saveAll(createAlarmTime(user, threeTakingTime));

		return new AlarmTimeResponse(savedAlarm, savedAlarmTimes);
	}

	// 용법용량에서 복용횟수, 복용량, 복용시기 파싱
	private Map<String, String> parseDosage(String dosage) {
		// TODO: 복용횟수, 복용량, 복용시기 파싱
		Map<String,String> map = new HashMap<>();

		map.put("number", "");	// 복용횟수 ex) 1일 1회
		map.put("amount", "");	// 복용량 ex) 1회 2정
		map.put("when", "");	// 복용시긴 ex) 식전 30분
		return map;
	}

	// 상대복용시간 계산
	private Integer[] getThreeTakingTime(String number, String when) {
		// TODO: 복용횟수와 복용시기를 통해 상대복용시간 계산
		Integer[] threeTakingTime = new Integer[3];	// 아침,점심,저녁식사 기준 상대복용시간 (단위:분) (ex. 1일 2회, 식전 30분: [-30,null,-30])

		return threeTakingTime;
	}

	// 복용 시간대를 기준으로 AlarmTime 객체 생성
	private List<AlarmTime> createAlarmTime(User user, Integer[] threeTakingTime) {
		List<AlarmTime> result = new ArrayList<>();

		MealTime mealTime = mealTimeRepository.findById(user.getUserIdx())
				.orElseThrow(() -> BadRequestException.NOT_EXIST_MEAL_TIME);

		LocalTime[] mealTimes = { mealTime.getMorning(), mealTime.getLunch(), mealTime.getDinner() };
		for (int i = 0; i < 3; i++) {
			if (threeTakingTime[i] != null) {
				LocalTime time = mealTimes[i].plusMinutes(threeTakingTime[i]);
				AlarmTime alarmTime = AlarmTime.builder()
						.time(time)
						.build();
				result.add(alarmTime);
			}
		}

		return result;
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
