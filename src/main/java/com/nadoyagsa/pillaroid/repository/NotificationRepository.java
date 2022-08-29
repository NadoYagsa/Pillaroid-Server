package com.nadoyagsa.pillaroid.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nadoyagsa.pillaroid.entity.Medicine;
import com.nadoyagsa.pillaroid.entity.Notification;
import com.nadoyagsa.pillaroid.entity.User;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
	Optional<Notification> findByUserAndMedicine(User user, Medicine medicine);

	@Query("SELECT f FROM Notification f WHERE f.user.userIdx = :userIdx AND f.medicine.medicineIdx = :medicineIdx")
	Optional<Notification> findByUserIdxAndMedicineIdx(@Param("userIdx") long userIdx, @Param("medicineIdx") int medicineIdx);

	List<Notification> findByUser(User user);
}
