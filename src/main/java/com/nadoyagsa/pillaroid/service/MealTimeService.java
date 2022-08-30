package com.nadoyagsa.pillaroid.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nadoyagsa.pillaroid.common.exception.NotFoundException;
import com.nadoyagsa.pillaroid.dto.MealTimeDto;
import com.nadoyagsa.pillaroid.dto.MealTimeResponse;
import com.nadoyagsa.pillaroid.entity.MealTime;
import com.nadoyagsa.pillaroid.entity.User;
import com.nadoyagsa.pillaroid.repository.MealTimeRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MealTimeService {

	private final MealTimeRepository mealTimeRepository;

	// 복용 시간대 조회
	public MealTimeResponse findMealTimeByUser(User user) {
		long userIdx = user.getUserIdx();
		MealTime mealTime = mealTimeRepository.findById(userIdx)
				.orElseThrow(() -> NotFoundException.DATA_NOT_FOUND);
		return mealTime.toMealTimeResponse();
	}

	// 복용 시간대 저장
	@Transactional
	public MealTimeResponse saveMealTime(User user, MealTimeDto mealTimeDto) {
		// 사용자의 복용 시간대 조회
		Optional<MealTime> optMealTime = mealTimeRepository.findById(user.getUserIdx());

		MealTime mealTime;
		if (optMealTime.isPresent()) {		// 조회 O
			mealTime = optMealTime.get();
			mealTime.updateMealTime(mealTimeDto);	// 복용 시간대 업데이트
		} else {							// 조회 X
			mealTime = mealTimeDto.toEntity(user);	// 복용 시간대 생성
		}

		mealTimeRepository.save(mealTime);

		return mealTime.toMealTimeResponse();
	}
}
