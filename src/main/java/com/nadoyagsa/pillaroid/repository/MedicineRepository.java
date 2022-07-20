package com.nadoyagsa.pillaroid.repository;

import com.nadoyagsa.pillaroid.entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicineRepository extends JpaRepository <Medicine, Integer> {
    // 의약품 번호로 의약품 조회
    Optional<Medicine> findMedicineByMedicineIdx(int medicineIdx);

    // 품목일련번호로 의약품 조회
    Optional<Medicine> findMedicineBySerialNumber(int serialNumber);

    // 표준코드로 의약품 조회
    Optional<Medicine> findMedicineByStandardCode(String standardCode);

    // 의약품명으로 시작하는 의약품 조회 (in case search)
    List<Medicine> findAllByNameStartingWith(String title);

    // 의약품명을 포함하는 의약품 조회 (in voice search)
    List<Medicine> findAllByNameContaining(String name);
}
