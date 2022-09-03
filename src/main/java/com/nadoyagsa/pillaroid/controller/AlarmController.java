package com.nadoyagsa.pillaroid.controller;

import com.nadoyagsa.pillaroid.common.dto.ApiResponse;
import com.nadoyagsa.pillaroid.common.exception.ForbiddenException;
import com.nadoyagsa.pillaroid.common.exception.InternalServerException;
import com.nadoyagsa.pillaroid.common.exception.UnauthorizedException;
import com.nadoyagsa.pillaroid.dto.AlarmDto;
import com.nadoyagsa.pillaroid.dto.AlarmResponse;
import com.nadoyagsa.pillaroid.entity.User;
import com.nadoyagsa.pillaroid.jwt.AuthTokenProvider;
import com.nadoyagsa.pillaroid.service.AlarmService;
import com.nadoyagsa.pillaroid.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/user/alarm")
public class AlarmController {
    private final AuthTokenProvider authTokenProvider;
    private final UserService userService;
    private final AlarmService alarmService;

    @Autowired
    public AlarmController(AuthTokenProvider authTokenProvider, UserService userService, AlarmService alarmService) {
        this.authTokenProvider = authTokenProvider;
        this.userService = userService;
        this.alarmService = alarmService;
    }

    // 의약품에 대한 사용자 알림 조회
    @GetMapping("/{mid}")
    public ApiResponse<AlarmResponse> getUserAlarm(HttpServletRequest request, @PathVariable("mid") int medicineIdx) {
        User user = findUserByToken(request)
                .orElseThrow(() -> UnauthorizedException.UNAUTHORIZED_USER);

        AlarmResponse alarm = alarmService.findAlarmByUserAndMedicineIdx(user, medicineIdx);
        return ApiResponse.success(alarm);
    }

    // 사용자의 의약품 알림 목록 조회
    @GetMapping("/list")
    public ApiResponse<List<AlarmResponse>> getUserAlarmList(HttpServletRequest request) {
        User user = findUserByToken(request)
                .orElseThrow(() -> UnauthorizedException.UNAUTHORIZED_USER);

        List<AlarmResponse> alarms = alarmService.findAlarmByUser(user);
        return ApiResponse.success(alarms);
    }

    // 의약품에 대한 사용자 알림 등록
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ApiResponse<AlarmResponse> saveUserAlarm(HttpServletRequest request, @RequestBody AlarmDto alarmDto) {
        User user = findUserByToken(request)
                .orElseThrow(() -> UnauthorizedException.UNAUTHORIZED_USER);

        AlarmResponse alarmResponse = alarmService.saveAlarm(user, alarmDto);
        return ApiResponse.success(alarmResponse);
    }

    // 의약품에 대한 사용자 알림 삭제
    @DeleteMapping("/{aid}")
    public ApiResponse<String> deleteUserAlarm(HttpServletRequest request, @PathVariable("aid")  long alarmIdx) throws ForbiddenException {
        User user = findUserByToken(request)
                .orElseThrow(() -> UnauthorizedException.UNAUTHORIZED_USER);

        alarmService.deleteAlarm(user, alarmIdx);  // 알림 데이터 삭제
        return ApiResponse.SUCCESS;
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
