package com.nadoyagsa.pillaroid.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nadoyagsa.pillaroid.entity.User;
import com.nadoyagsa.pillaroid.jwt.AuthTokenProvider;
import com.nadoyagsa.pillaroid.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
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

    // 카카오 로그인 (Input: access token)
    @PostMapping("/kakao")
    public ResponseEntity<Map<String, Object>> kakaoLogin(@RequestBody Map<String, String> requestBody) {
        String accessToken = requestBody.get("access_token");

        HashMap<String, Object> response = new HashMap<>(); // 서버->클라이언트 응답

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
            response.put("success", false);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);              // Status Code=400
        }

        ObjectMapper objectMapper = new ObjectMapper();
        Long kakaoUserId;
        try {
            JsonNode userInfo = objectMapper.readTree(kakaoResponse.getBody());
            kakaoUserId = userInfo.path("id").asLong();
        } catch (JsonProcessingException e) {
            response.put("success", false);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);    // Status Code=500
        }

        String authToken = authTokenProvider.createAuthToken(kakaoUserId);
        Optional<User> user = userService.findUserByKakaoAccountId(kakaoUserId);
        // 클라이언트의 로그인 경험 있음
        if (user.isPresent()) {
            response.put("success", true);
            response.put("authToken", authToken);
            //response.put("user", user);
            return new ResponseEntity<>(response, HttpStatus.OK);                       // Status Code=200
        }
        // 클라이언트의 로그인 경험 없음(DB에 사용자 추가)
        else {
            User newUser = userService.signUp(new User(kakaoUserId));

            response.put("success", true);
            response.put("authToken", authToken);
            //response.put("user", newUser);
            return new ResponseEntity<>(response, HttpStatus.CREATED);                  // Status Code=201
        }
    }

    // 자동로그인은 시각장애인을 위해 프론트에서 authToken값 존재 여부에 따라 수행됨
}
