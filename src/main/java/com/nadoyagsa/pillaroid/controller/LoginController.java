package com.nadoyagsa.pillaroid.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nadoyagsa.pillaroid.common.dto.ApiResponse;
import com.nadoyagsa.pillaroid.common.exception.BadRequestException;
import com.nadoyagsa.pillaroid.common.exception.InternalServerException;
import com.nadoyagsa.pillaroid.dto.LoginDTO;
import com.nadoyagsa.pillaroid.dto.LoginResponse;
import com.nadoyagsa.pillaroid.entity.User;
import com.nadoyagsa.pillaroid.jwt.AuthTokenProvider;
import com.nadoyagsa.pillaroid.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@RestController
@RequestMapping(value = "/login")
public class LoginController {
    private final UserService userService;
    private final AuthTokenProvider authTokenProvider;

    @Autowired
    public LoginController(UserService userService, AuthTokenProvider authTokenProvider) {
        this.userService = userService;
        this.authTokenProvider = authTokenProvider;
    }

    // 카카오 로그인 (Input: access_token)
    @PostMapping("/kakao")
    public ApiResponse<LoginResponse> kakaoLogin(@RequestBody LoginDTO loginDTO) {
        String accessToken = loginDTO.getAccessToken();
        String alarmToken = loginDTO.getAlarmToken() == null ? "" : loginDTO.getAlarmToken();

        RestTemplate restTemplate = new RestTemplate();     // Spring의 HTTP 통신 템플릿
        // 카카오로 보낼 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        // 카카오와의 통신
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        ResponseEntity<String> kakaoResponse;
        try {
            kakaoResponse = restTemplate.exchange(
                    "https://kapi.kakao.com/v2/user/me",
                    HttpMethod.POST,
                    kakaoUserInfoRequest,
                    String.class
            );
        } catch (Exception e) {     // 카카오로 요청 실패
            throw BadRequestException.BAD_PARAMETER;            // Status Code=400
        }

        ObjectMapper objectMapper = new ObjectMapper();
        Long kakaoUserId;
        try {
            JsonNode userInfo = objectMapper.readTree(kakaoResponse.getBody());
            kakaoUserId = userInfo.path("id").asLong();
        } catch (JsonProcessingException e) {
            throw InternalServerException.INTERNAL_ERROR;       // Status Code=500
        }

        Optional<User> user = userService.findUserByKakaoAccountId(kakaoUserId);
        // 클라이언트의 로그인 경험 있음
        if (user.isPresent()) {
            // 알람 토큰이 다르다면 갱신
            if (!user.get().getAlarmToken().equals(alarmToken) && !alarmToken.equals("")) {
                user.get().setAlarmToken(alarmToken);
                userService.save(user.get());
            }

            String authToken = authTokenProvider.createAuthToken(user.get().getUserIdx());      // 토큰 생성

            LoginResponse response = LoginResponse.builder()
                    .authToken(authToken)
                    .build();

            return ApiResponse.success(response);
        }
        // 클라이언트의 로그인 경험 없음(DB에 사용자 추가)
        else {
            User newUser = User.builder()
                    .kakaoAccountId(kakaoUserId)
                    .alarmToken(alarmToken)
                    .build();
            newUser = userService.signUp(newUser);

            String authToken = authTokenProvider.createAuthToken(newUser.getUserIdx());

            LoginResponse response = LoginResponse.builder()
                    .authToken(authToken)
                    .build();

            return ApiResponse.success(response);
        }
    }

    // 자동로그인은 시각장애인을 위해 프론트에서 authToken값 존재 여부에 따라 수행됨
}
