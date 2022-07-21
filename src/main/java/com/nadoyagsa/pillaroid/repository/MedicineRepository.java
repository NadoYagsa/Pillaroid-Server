package com.nadoyagsa.pillaroid.repository;

import com.nadoyagsa.pillaroid.entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicineRepository extends JpaRepository <Medicine, Integer> {
    // 품목일련번호로 의약품 조회
    Optional<Medicine> findMedicineBySerialNumber(int serialNumber);

    // 표준코드로 의약품 조회
    Optional<Medicine> findMedicineByStandardCode(String standardCode);

    // 의약품명으로 시작하는 의약품 조회 (in case search && prescription search)
    @Query("SELECT m FROM Medicine m WHERE m.name like :name%")
    List<Medicine> findMedicinesByStartingName(@Param("name") String name);

    List<Medicine> findAllByNameContaining(String name);
}
