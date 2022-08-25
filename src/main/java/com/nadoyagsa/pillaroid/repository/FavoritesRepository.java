
package com.nadoyagsa.pillaroid.repository;

import com.nadoyagsa.pillaroid.entity.Favorites;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoritesRepository extends JpaRepository <Favorites, Long> {
    // 회원 idx로 즐겨찾기 목록 조회
    List<Favorites> findAllByUser_UserIdx(Long userIdx);

    // 회원 idx와 의약품 idx로 즐겨찾기 조회
    @Query("SELECT f FROM Favorites f WHERE f.user.userIdx = :userIdx AND f.medicine.medicineIdx = :medicineIdx")
    Optional<Favorites> findFavoritesByUserAndMedicine(@Param("userIdx") Long userIdx, @Param("medicineIdx") int medicineIdx);

    // 의약품 키워드로 즐겨찾기 조회
    @Query("SELECT f FROM Favorites f WHERE f.user.userIdx = :userIdx AND f.medicine.name like %:keyword%")
    List<Favorites> findFavoritesByKeyword(@Param("userIdx") Long userIdx, @Param("keyword") String keyword);
}
