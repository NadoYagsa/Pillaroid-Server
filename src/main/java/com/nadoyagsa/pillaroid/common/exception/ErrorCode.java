package com.nadoyagsa.pillaroid.common.exception;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ErrorCode {
	/* 400 BAD_REQUEST: 잘못된 요청 구문 */
	BAD_PARAMETER(40001, BAD_REQUEST, "요청 파라미터가 잘못되었습니다."),
	BAD_PARAMETER_TYPE(40002, BAD_REQUEST, "지원하지 않는 파라미터 형식입니다."),
	NOT_SUPPORTED_BARCODE_FORMAT(40003, BAD_REQUEST, "지원하지 않는 바코드 형식입니다."),
	NOT_EXIST_MEAL_TIME(40004, BAD_REQUEST, "복용 시간대가 설정되지 않았습니다."),
	CAN_NOT_CREATE_ALARM(40005, BAD_REQUEST, "의약품에서 알림을 위한 정보를 얻을 수 없어 알림을 생성할 수 없습니다."),

	/* 401 UNAUTHORIZED: 인증 자격 없음 */
	UNAUTHORIZED_USER(401, UNAUTHORIZED, "인증된 사용자가 아닙니다."),

	/* 403 FORBIDDEN: 권한 없음 */
	DELETE_FORBIDDEN(40301, FORBIDDEN, "삭제할 권한이 없습니다."),

	/* 404 NOT_FOUND: Resource 를 찾을 수 없음 */
	DATA_NOT_FOUND(40403, NOT_FOUND, "해당 정보가 없습니다."),
	BARCODE_NOT_FOUND(40401, NOT_FOUND, "해당 바코드에 대한 정보가 없습니다."),
	MEDICINE_NOT_FOUND(40402, NOT_FOUND, "조건에 맞는 의약품이 존재하지 않습니다."),

	/* 408 CONFLICT: 요청이 현재 서버의 상태와 충돌 */
	DATA_CONFLICT(40901, CONFLICT, "리소스 충돌이 발생했습니다."),

	/* 500 INTERNAL_SERVER_ERROR : 서버 오류 */
	INTERNAL_ERROR(500, INTERNAL_SERVER_ERROR, "서버 내부 오류로 인해 응답을 제공할 수 없습니다.");

	private final long errorIdx;	//에러 식별코드
	private final HttpStatus errorStatus;
	private final String detail;

	public long getErrorIdx() { return errorIdx; }
	public HttpStatus getErrorStatus() { return errorStatus; }
	public String getDetail() { return detail; }
}
