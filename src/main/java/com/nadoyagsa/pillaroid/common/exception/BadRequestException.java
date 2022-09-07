package com.nadoyagsa.pillaroid.common.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BadRequestException extends RuntimeException {
	public static final BadRequestException BAD_PARAMETER = new BadRequestException(ErrorCode.BAD_PARAMETER);
	public static final BadRequestException NOT_SUPPORTED_BARCODE_FORMAT = new BadRequestException(ErrorCode.NOT_SUPPORTED_BARCODE_FORMAT);
	public static final BadRequestException NOT_EXIST_MEAL_TIME = new BadRequestException(ErrorCode.NOT_EXIST_MEAL_TIME);
	public static final BadRequestException CAN_NOT_CREATE_ALARM = new BadRequestException(ErrorCode.CAN_NOT_CREATE_ALARM);

	private final ErrorCode errorCode;

	public ErrorCode getErrorCode() {
		return errorCode;
	}
}
