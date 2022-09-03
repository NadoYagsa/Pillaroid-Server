package com.nadoyagsa.pillaroid.controller;

import com.nadoyagsa.pillaroid.common.dto.ApiResponse;
import com.nadoyagsa.pillaroid.common.exception.BadRequestException;
import com.nadoyagsa.pillaroid.common.exception.InternalServerException;
import com.nadoyagsa.pillaroid.common.exception.UnauthorizedException;
import com.nadoyagsa.pillaroid.dto.MealTimeDto;
import com.nadoyagsa.pillaroid.dto.MealTimeResponse;
import com.nadoyagsa.pillaroid.dto.TokenDto;
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

    // 복용 알람을 위한 토큰 저장
    @PatchMapping("/alarm-token")
    public ApiResponse<String> patchUserNoticeToken(HttpServletRequest request, @RequestBody TokenDto tokenDto) {
        if (tokenDto.getToken() == null || tokenDto.getToken().equals(""))
            throw BadRequestException.BAD_PARAMETER;

        Optional<User> user = findUserByToken(request);
        if (user.isPresent()) {
            if (user.get().getAlarmToken() != null && user.get().getAlarmToken().equals(tokenDto.getToken())) {   // 토큰이 기존 토큰과 동일할 때
                return ApiResponse.SUCCESS;
            }
            else {  // 토큰이 갱신되고자 할 때
                user.get().setAlarmToken(tokenDto.getToken());
                if (userService.save(user.get())) {
                    return ApiResponse.SUCCESS;
                }
                else {
                    throw InternalServerException.INTERNAL_ERROR;
                }
            }
        }
        else
            throw UnauthorizedException.UNAUTHORIZED_USER;
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
