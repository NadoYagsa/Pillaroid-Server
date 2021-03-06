package com.nadoyagsa.pillaroid.common.exception;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ErrorCode {
	//TODO: 발생할 오류 사항 추가해나가면 됨

	/* 400 BAD_REQUEST: 잘못된 요청 구문 */
	BAD_PARAMETER(40001, BAD_REQUEST, "요청 파라미터가 잘못되었습니다."),
	BAD_PARAMETER_TYPE(40002, BAD_REQUEST, "지원하지 않는 파라미터 형식입니다."),
	NOT_SUPPORTED_BARCODE_FORMAT(40003, BAD_REQUEST, "지원하지 않는 바코드 형식입니다."),

	/* 404 NOT_FOUND : Resource 를 찾을 수 없음 */
	DATA_NOT_FOUND(40403, NOT_FOUND, "해당 정보가 없습니다."),
	BARCODE_NOT_FOUND(40401, NOT_FOUND, "해당 바코드에 대한 정보가 없습니다."),
	MEDICINE_NOT_FOUND(40402, NOT_FOUND, "조건에 맞는 의약품이 존재하지 않습니다."),

	/* 500 INTERNAL_SERVER_ERROR : 서버 오류 */
	INTERNAL_ERROR(50001, INTERNAL_SERVER_ERROR, "서버 내부 오류로 인해 응답을 제공할 수 없습니다.");

	private final long errorIdx;	//에러 식별코드
	private final HttpStatus errorStatus;
	private final String detail;

	public long getErrorIdx() { return errorIdx; }
	public HttpStatus getErrorStatus() { return errorStatus; }
	public String getDetail() { return detail; }
}
