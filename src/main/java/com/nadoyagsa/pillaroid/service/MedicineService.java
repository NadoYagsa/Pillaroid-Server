package com.nadoyagsa.pillaroid.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.nadoyagsa.pillaroid.common.exception.NotFoundException;
import com.nadoyagsa.pillaroid.component.MedicineExcelUtils;
import com.nadoyagsa.pillaroid.dto.MedicineResponse;

import com.nadoyagsa.pillaroid.dto.NotificationResponse;
import com.nadoyagsa.pillaroid.dto.PrescriptionResponse;
import com.nadoyagsa.pillaroid.dto.VoiceResponse;
import com.nadoyagsa.pillaroid.entity.Favorites;
import com.nadoyagsa.pillaroid.entity.Medicine;
import com.nadoyagsa.pillaroid.entity.Notification;
import com.nadoyagsa.pillaroid.repository.FavoritesRepository;
import com.nadoyagsa.pillaroid.repository.MedicineRepository;
import com.nadoyagsa.pillaroid.repository.NotificationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MedicineService {
    private final MedicineRepository medicineRepository;
    private final MedicineExcelUtils medicineExcelUtils;
    private final FavoritesRepository favoritesRepository;
    private final NotificationRepository notificationRepository;

    @Autowired
    public MedicineService(MedicineRepository medicineRepository, MedicineExcelUtils medicineExcelUtils,
            FavoritesRepository favoritesRepository,
            NotificationRepository notificationRepository) {
        this.medicineRepository = medicineRepository;
        this.medicineExcelUtils = medicineExcelUtils;
        this.favoritesRepository = favoritesRepository;
        this.notificationRepository = notificationRepository;
    }

    public Optional<MedicineResponse> getMedicineInfoByIdx(int idx) {
        Optional<Medicine> medicine = medicineRepository.findById(idx);
        if (medicine.isEmpty())
            return Optional.empty();
        else
            return Optional.ofNullable(medicine.get().toMedicineResponse());
    }

    public Optional<MedicineResponse> getMedicineInfoBySerialNumber(int serialNumber) {
        Optional<Medicine> medicine = medicineRepository.findMedicineBySerialNumber(serialNumber);
        if (medicine.isEmpty())
            return Optional.empty();
        else
            return Optional.ofNullable(medicine.get().toMedicineResponse());
    }

    public Optional<MedicineResponse> getMedicineInfoByStandardCode(String barcode) {
        Optional<Medicine> medicine = medicineRepository.findMedicineByStandardCode(barcode);
        if (medicine.isEmpty())
            return Optional.empty();
        else
            return Optional.ofNullable(medicine.get().toMedicineResponse());
    }

    public List<VoiceResponse> getMedicineListByName(String name) {
        List<Medicine> medicineList = medicineRepository.findMedicinesByContainingName(name);

        if (medicineList.size() == 0)
            throw NotFoundException.MEDICINE_NOT_FOUND;
        else
            return medicineList.stream().map(Medicine::toVoiceResponse).collect(Collectors.toList());
    }

    public List<PrescriptionResponse> getMedicineListByNameList(String[] nameList) {
        List<PrescriptionResponse> prescriptionList = new ArrayList<>();

        for (String name : nameList) {
            List<Medicine> medicineList = medicineRepository.findMedicinesByStartingName(name);

            // DB에 저장된 제품명에서 괄호를 제거하고 동일한 의약품명이 있다면 해당 의약품 정보 전달 (else 가장 먼저 조회된 결과 전달)
            boolean isAdded = false;
            for (Medicine medicine : medicineList) {
                if (medicine.getName().strip().equals(name)) {
                    prescriptionList.add(medicine.toPrescriptionResponse(null, null));

                    isAdded = true;
                    break;
                }
            }

            if (!isAdded)
                prescriptionList.add(medicineList.get(0).toPrescriptionResponse(null, null));
        }
        return prescriptionList;
    }

    public List<PrescriptionResponse> getMedicineListByNameList(String[] nameList, Long userIdx) {
        List<PrescriptionResponse> prescriptionList = new ArrayList<>();
        for (String name : nameList) {
            List<Medicine> medicineList = medicineRepository.findMedicinesByStartingName(name);

            // DB에 저장된 제품명에서 괄호를 제거하고 동일한 의약품명이 있다면 해당 의약품 정보 전달 (else 가장 먼저 조회된 결과 전달)
            boolean isAdded = false;
            for (Medicine medicine : medicineList) {
                if (medicine.getName().strip().equals(name)) {
                    Optional<Favorites> favorites = findFavoritesByUserAndMedicineIdx(userIdx, medicine.getMedicineIdx());
                    Optional<Notification> notification = findNotificationByUserAndMedicineIdx(userIdx, medicine.getMedicineIdx());

                    // Optional에 값이 없으면 null로 저장
                    Long favoritesIdx = favorites.map(Favorites::getFavoritesIdx).orElse(null);
                    NotificationResponse notificationResponse = notification.map(Notification::toNotificationResponse).orElse(null);

                    prescriptionList.add(medicine.toPrescriptionResponse(favoritesIdx, notificationResponse));

                    isAdded = true;
                    break;
                }
            }

            if (!isAdded) {
                Medicine firstMedicine = medicineList.get(0);
                Optional<Favorites> favorites = findFavoritesByUserAndMedicineIdx(userIdx, firstMedicine.getMedicineIdx());
                Optional<Notification> notification = findNotificationByUserAndMedicineIdx(userIdx, firstMedicine.getMedicineIdx());

                // Optional에 값이 없으면 null로 저장
                Long favoritesIdx = favorites.map(Favorites::getFavoritesIdx).orElse(null);
                NotificationResponse notificationResponse = notification.map(Notification::toNotificationResponse).orElse(null);

                prescriptionList.add(firstMedicine.toPrescriptionResponse(favoritesIdx, notificationResponse));
            }
        }
        return prescriptionList;
    }


    // 의약품 번호와 회원 번호로 즐겨찾기 조회
    public Optional<Favorites> findFavoritesByUserAndMedicineIdx(Long userIdx, int medicineIdx) {
        return favoritesRepository.findFavoritesByUserAndMedicine(userIdx, medicineIdx);
    }

    // 의약품에 해당하는 사용자 알림 조회
    public Optional<Notification> findNotificationByUserAndMedicineIdx(Long userIdx, int medicineIdx) {
        return notificationRepository.findByUserIdxAndMedicineIdx(userIdx, medicineIdx);
    }

    public boolean updateMedicineInfoInExcel() {
        try {
            medicineExcelUtils.updateMedicineExcel();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
