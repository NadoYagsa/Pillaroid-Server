package com.nadoyagsa.pillaroid.service;

import com.nadoyagsa.pillaroid.common.exception.InternalServerException;
import com.nadoyagsa.pillaroid.entity.User;
import com.nadoyagsa.pillaroid.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserById(Long userIdx) {
        return userRepository.getById(userIdx);
    }

    public Optional<User> findUserById(Long userIdx) {
        return userRepository.findById(userIdx);
    }

    public Optional<User> findUserByKakaoAccountId(Long kakaoAccountId) {
        return userRepository.findUserByKakaoAccountId(kakaoAccountId);
    }

    public User signUp(User user) {
        return userRepository.saveAndFlush(user);
    }

    public boolean save(User user) {
        try {
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            throw InternalServerException.INTERNAL_ERROR;
        }
    }
}
