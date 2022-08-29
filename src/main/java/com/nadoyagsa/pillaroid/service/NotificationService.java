package com.nadoyagsa.pillaroid.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nadoyagsa.pillaroid.common.exception.BadRequestException;
import com.nadoyagsa.pillaroid.common.exception.ForbiddenException;
import com.nadoyagsa.pillaroid.common.exception.NotFoundException;
import com.nadoyagsa.pillaroid.dto.NotificationResponse;
import com.nadoyagsa.pillaroid.dto.NotificationTimeDto;
import com.nadoyagsa.pillaroid.dto.NotificationTimeResponse;
import com.nadoyagsa.pillaroid.entity.Medicine;
import com.nadoyagsa.pillaroid.entity.Notification;
import com.nadoyagsa.pillaroid.entity.NotificationTime;
import com.nadoyagsa.pillaroid.entity.User;
import com.nadoyagsa.pillaroid.repository.MedicineRepository;
import com.nadoyagsa.pillaroid.repository.NotificationRepository;
import com.nadoyagsa.pillaroid.repository.NotificationTimeRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class NotificationService {

	private final NotificationRepository notificationRepository;
	private final NotificationTimeRepository notificationTimeRepository;
	private final MedicineRepository medicineRepository;

	// 의약품에 해당하는 사용자 알림 조회
	public NotificationResponse findNotificationByUserAndMedicineIdx(User user, int medicineIdx) {
		Medicine medicine = medicineRepository.findById(medicineIdx)
				.orElseThrow(() -> BadRequestException.BAD_PARAMETER);
		Notification notification = notificationRepository.findByUserAndMedicine(user, medicine)
				.orElseThrow(() -> NotFoundException.DATA_NOT_FOUND);
		return notification.toNotificationResponse();
	}

	// 사용자 관련 알림 목록 조회
	public List<NotificationResponse> findNotificationByUser(User user) {
		List<Notification> notifications = notificationRepository.findByUser(user);
		return notifications.stream()
				.map(Notification::toNotificationResponse)
				.collect(Collectors.toList());
	}

	// 의약품에 해당하는 사용자 알림 삭제
	@Transactional
	public NotificationTimeResponse deleteNotification(User user, long notificationIdx) throws NotFoundException, ForbiddenException {
		// 알림 데이터가 있는지 검사
		Notification notification = notificationRepository.findById(notificationIdx)
				.orElseThrow(() -> NotFoundException.DATA_NOT_FOUND);

		// 사용자 본인이 데이터를 지우는 것인지 검사
		if (!Objects.equals(notification.getUser().getUserIdx(), user.getUserIdx())) {
			throw ForbiddenException.deleteForbidden;
		}

		List<NotificationTime> notificationTimes = notificationTimeRepository.findByNotification(notification);		// 삭제할 알림과 관련된 시간 데이터 조회
		notificationRepository.deleteById(notification.getNotificationIdx());	// 데이터 삭제

		// 전달할 Response 생성
		List<NotificationTimeDto> notificationTimeDtos = notificationTimes.stream()
				.map(NotificationTime::toNotificationTimeDto)
				.collect(Collectors.toList());

		return NotificationTimeResponse.builder()
				.notificationIdx(notificationIdx)
				.notificationTimes(notificationTimeDtos)
				.build();
	}
}
