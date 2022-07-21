package com.nadoyagsa.pillaroid.repository;

import com.nadoyagsa.pillaroid.entity.Appearance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppearanceRepository extends JpaRepository <Appearance, Integer> {
}
