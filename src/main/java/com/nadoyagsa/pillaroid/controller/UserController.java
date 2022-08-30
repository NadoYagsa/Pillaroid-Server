package com.nadoyagsa.pillaroid.controller;

import com.nadoyagsa.pillaroid.common.dto.ApiResponse;
import com.nadoyagsa.pillaroid.common.exception.InternalServerException;
import com.nadoyagsa.pillaroid.common.exception.UnauthorizedException;
import com.nadoyagsa.pillaroid.dto.MealTimeDto;
import com.nadoyagsa.pillaroid.dto.MealTimeResponse;
import com.nadoyagsa.pillaroid.entity.User;
import com.nadoyagsa.pillaroid.jwt.AuthTokenProvider;
import com.nadoyagsa.pillaroid.service.MealTimeService;
import com.nadoyagsa.pillaroid.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.util.Optional;

@RestController
@RequestMapping(value = "/user")
public class UserController {
    private final AuthTokenProvider authTokenProvider;
    private final UserService userService;
    private final MealTimeService mealTimeService;

    @Autowired
    public UserController(AuthTokenProvider authTokenProvider, UserService userService, MealTimeService mealTimeService) {
        this.authTokenProvider = authTokenProvider;
        this.userService = userService;
        this.mealTimeService = mealTimeService;
    }

    /* 복용 시간대 */
    // 사용자의 복용 시간대 조회
    @GetMapping("/mealtime")
    public ApiResponse<MealTimeResponse> getUserMealTime(HttpServletRequest request) {
        User user = findUserByToken(request)
                .orElseThrow(() -> UnauthorizedException.UNAUTHORIZED_USER);

        MealTimeResponse mealTime = mealTimeService.findMealTimeByUser(user);
        return ApiResponse.success(mealTime);
    }

    // 사용자의 복용 시간대 추가 및 수정
    @PostMapping("/mealtime")
    public ApiResponse<MealTimeResponse> saveUserMealTime(HttpServletRequest request, @RequestBody @Valid MealTimeDto mealTimeDto) {
        User user = findUserByToken(request)
                .orElseThrow(() -> UnauthorizedException.UNAUTHORIZED_USER);

        MealTimeResponse mealTime = mealTimeService.saveMealTime(user, mealTimeDto);
        return ApiResponse.success(mealTime);
    }

    // 사용자 jwt 토큰으로부터 회원 정보 조회
    public Optional<User> findUserByToken(HttpServletRequest request) {
        try {
            // 접근 사용자 조회
            Long userIdx = authTokenProvider.getClaims(request.getHeader("authorization")).get("userId", Long.class);

            return userService.findUserById(userIdx);
        } catch (Exception e) {
            throw InternalServerException.INTERNAL_ERROR;
        }
    }
}
