package com.nadoyagsa.pillaroid.repository;

import com.nadoyagsa.pillaroid.entity.Appearance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppearanceRepository extends JpaRepository <Appearance, Integer> {
    // 의약품 번호로 의약품 정보(외형+이외 정보) 조회
    Optional<Appearance> findByMedicine_MedicineIdx(int medicineIdx);
}
