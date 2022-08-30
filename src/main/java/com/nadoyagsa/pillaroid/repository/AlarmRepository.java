package com.nadoyagsa.pillaroid.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nadoyagsa.pillaroid.entity.Medicine;
import com.nadoyagsa.pillaroid.entity.Alarm;
import com.nadoyagsa.pillaroid.entity.User;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
	Optional<Alarm> findByUserAndMedicine(User user, Medicine medicine);

	@Query("SELECT f FROM Alarm f WHERE f.user.userIdx = :userIdx AND f.medicine.medicineIdx = :medicineIdx")
	Optional<Alarm> findByUserIdxAndMedicineIdx(@Param("userIdx") long userIdx, @Param("medicineIdx") int medicineIdx);

	List<Alarm> findByUser(User user);
}
