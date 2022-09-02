package com.nadoyagsa.pillaroid.service;

import static java.time.temporal.ChronoUnit.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

		MealTime mealTime = mealTimeRepository.findById(user.getUserIdx())
				.orElseThrow(() -> BadRequestException.NOT_EXIST_MEAL_TIME);

		Map<String, String> dosageSummary = parseDosage(medicine.getDosage());
		Integer[] threeTakingTime = getThreeTakingTime(mealTime, dosageSummary.get("number"), dosageSummary.get("when"));

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
		List<AlarmTime> savedAlarmTimes = alarmTimeRepository.saveAll(createAlarmTime(alarm, mealTime, threeTakingTime));

		return new AlarmTimeResponse(savedAlarm, savedAlarmTimes);
	}

	// 용법용량에서 복용횟수, 복용량, 복용시기 파싱
	private Map<String, String> parseDosage(String dosage) {
		String number = "";
		String amount = "";
		String when = "";

		// "셋째날부터" 가 있으면 그 이후부터 찾음
		String[] split = dosage.split("셋째날부터");
		String commonDosage = split.length == 1 ? dosage : split[1];

		Pattern patternNumber = Pattern.compile("(1일)\\s([0-9]\\s?([~～-])\\s?)?[0-9]\\s?([회번])");	// 복용횟수 찾기
		Matcher matcherNumber = patternNumber.matcher(commonDosage);
		if (matcherNumber.find()) {
			number = matcherNumber.group().strip();
		}

		Pattern patternAmount = Pattern.compile("([0-9]+\\s?([~～-])?\\s?[0-9]?(정|개|캡슐|캅셀|병|포|팩|환)(?!월))");	// 복용량 찾기
		Matcher matcherAmount = patternAmount.matcher(commonDosage);
		if (matcherAmount.find()) {
			amount = matcherAmount.group().strip();
		}

		Pattern patternWhen = Pattern.compile("((식사\\s?전)|(식사\\s?후)|식전|식후|식간)\\s*(([1-6]{1}[0-9]{1})\\s?([~～-])?\\s?([1-6]{1}[0-9]{1})?분)?([1-7](시간))?");	// 복용시기 찾기
		Matcher matcherWhen = patternWhen.matcher(commonDosage);
		if (matcherWhen.find()) {
			when = matcherWhen.group().strip();

			when = when.replaceFirst("식사\\s?", "식");
		}

		Map<String, String> map = new HashMap<>();
		map.put("number", number);	// 복용횟수 ex) 1일 1회, 1일 1번
		map.put("amount", amount);	// 복용량   ex) 2정
		map.put("when", when);		// 복용시간 ex) 식전 30분
		return map;
	}

	// 상대복용시간 계산
	private Integer[] getThreeTakingTime(MealTime mealTime, String number, String when) {
		Integer[] threeTakingTime = new Integer[3];	// 아침,점심,저녁식사 기준 상대복용시간 (단위:분) (ex. [-30,null,-30])

		if (number.equals("")) {
			throw BadRequestException.CAN_NOT_CREATE_ALARM;
		}

		int num = calculateNum(number);		// 복용횟수
		try {
			int signedMinute = calculateMinute(when);	// 상대복용시간

			if (num == 1) {
				threeTakingTime[0] = signedMinute;	// 아침
			} else if (num == 2) {
				threeTakingTime[0] = signedMinute;	// 아침
				threeTakingTime[2] = signedMinute;	// 저녁
			} else if (num == 3) {
				threeTakingTime[0] = signedMinute;	// 아침
				threeTakingTime[1] = signedMinute;	// 점심
				threeTakingTime[2] = signedMinute;	// 저녁
			}
		} catch (IllegalStateException e) {		// 식간
			if (num == 1) {
				long betweenMorningAndLunch = MINUTES.between(mealTime.getMorning(), mealTime.getLunch());
				threeTakingTime[0] = Long.valueOf(betweenMorningAndLunch).intValue() / 2;
			} else if (num == 2) {
				long betweenMorningAndLunch = MINUTES.between(mealTime.getMorning(), mealTime.getLunch());
				threeTakingTime[0] = Long.valueOf(betweenMorningAndLunch).intValue() / 2;

				long betweenLunchAndDinner = MINUTES.between(mealTime.getLunch(), mealTime.getDinner());
				threeTakingTime[1] = Long.valueOf(betweenLunchAndDinner).intValue() / 2;
			} else {
				throw BadRequestException.CAN_NOT_CREATE_ALARM;
			}
		}

		return threeTakingTime;
	}

	// 복용 횟수에서 숫자만 추출
	private int calculateNum(String number) {
		number = number.replaceAll(" |1일|회|번", "");
		String[] split = number.split("[~～-]");
		int min = Integer.parseInt(split[0]);
		if (min > 3) {
			throw BadRequestException.CAN_NOT_CREATE_ALARM;
		}
		return min;
	}

	// 문자로 된 상대 복용 시간을 숫자로 변환
	private int calculateMinute(String when) {
		when = when.replace(" ", "");

		if (when.equals("식간")) {
			throw new IllegalStateException();
		}

		if (when.equals("")) {	// 복용 시간이 없다면 식전 30분으로 설정
			return -30;
		}

		int time = 1;
		if (when.contains("식전")) {
			time *= -1;
		}

		if (when.contains("시간")) {
			time *= Integer.parseInt(when.replaceAll("식전|식후|시간", ""));
			time *= 60;
		} else if (when.contains("분")) {
			time *= Integer.parseInt(when.replaceAll("식전|식후|분", ""));
		} else {	// 식전/식후만 있고 시간 단위가 없다면 30분으로 설정
			time *= 30;
		}

		return time;
	}

	// 복용 시간대를 기준으로 AlarmTime 객체 생성
	private List<AlarmTime> createAlarmTime(Alarm alarm, MealTime mealTime, Integer[] threeTakingTime) {
		List<AlarmTime> result = new ArrayList<>();

		LocalTime[] mealTimes = { mealTime.getMorning(), mealTime.getLunch(), mealTime.getDinner() };
		for (int i = 0; i < 3; i++) {
			if (threeTakingTime[i] != null) {
				LocalTime time = mealTimes[i].plusMinutes(threeTakingTime[i]);
				AlarmTime alarmTime = AlarmTime.builder()
						.alarm(alarm)
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
