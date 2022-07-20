package com.nadoyagsa.pillaroid.repository;

import com.nadoyagsa.pillaroid.entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicineRepository extends JpaRepository <Medicine, Integer> {
    // 품목일련번호로 의약품 조회
    Optional<Medicine> findMedicineBySerialNumber(int serialNumber);

    // 표준코드로 의약품 조회
    Optional<Medicine> findMedicineByStandardCode(String standardCode);

    // 의약품명으로 시작하는 의약품 조회 (in case search && prescription search)      //TODO: 맨 위를 할 지는 고려해야 함
    Optional<Medicine> findFirstByNameStartingWith(String title);

    // 의약품명을 포함하는 의약품 조회 (in voice search)
    List<Medicine> findAllByNameContaining(String name);

    // TODO: 처방전 이름 list를 받았을 시 전달받은 리스트의 이름으로 시작하는 의약품을 찾는 방법 연구 필요! (StartingWith와 IN의 결합)
}
