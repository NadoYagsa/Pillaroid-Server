package com.nadoyagsa.pillaroid.common.exception;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ErrorCode {
	//TODO: 발생할 오류 사항 추가해나가면 됨

	/* 400 BAR_REQUEST: 잘못된 요청 구문 */
	NOT_SUPPORTED_BARCODE_FORMAT(BAD_REQUEST, "지원하지 않는 바코드 형식입니다."),

	/* 404 NOT_FOUND : Resource 를 찾을 수 없음 */
	BARCODE_NOT_FOUND(NOT_FOUND, "해당 바코드에 대한 정보가 없습니다.");

	private final HttpStatus errorStatus;
	private final String detail;

	public HttpStatus getErrorStatus() { return errorStatus; }
	public String getDetail() { return detail; }
}
