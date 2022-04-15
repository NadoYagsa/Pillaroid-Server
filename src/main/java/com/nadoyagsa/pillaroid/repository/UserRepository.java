package com.nadoyagsa.pillaroid.repository;

import com.nadoyagsa.pillaroid.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository <User, Long> {
    Optional<User> findUserByKakaoAccountId(Long kakaoAccountId);
}
