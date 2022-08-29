package com.nadoyagsa.pillaroid.common.dto;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.nadoyagsa.pillaroid.common.exception.ErrorCode;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(Include.NON_NULL)
public class ApiResponse<T> {	//TODO: 추후, 구체적인 오류사항이 담긴 Error 객체 만들고 ApiUtil로 클래스 바꾸기
	public static final ApiResponse<String> SUCCESS = success("OK");	//body 없는 200 response

	private Long errorIdx;
	private HttpStatus errorStatusCode;
	private String message;
	private T data;

	public static <T> ApiResponse<T> success(T data) {
		return new ApiResponse<T>(null, null, null, data);
	}

	public static <T> ApiResponse<T> error(ErrorCode errorCode) {
		return new ApiResponse<>(errorCode.getErrorIdx(), errorCode.getErrorStatus(), errorCode.getDetail(), null);
	}

	// 상세 오류 메시지를 포함
	public static <T> ApiResponse<T> error(ErrorCode errorCode, String causeMessage) {
		String message = String.format("%s(%s)", errorCode.getDetail(), causeMessage);
		return new ApiResponse<>(errorCode.getErrorIdx(), errorCode.getErrorStatus(), message, null);
	}
}
