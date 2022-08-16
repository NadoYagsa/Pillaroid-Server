package com.nadoyagsa.pillaroid.common.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InternalServerException extends RuntimeException {
	public static final InternalServerException INTERNAL_ERROR = new InternalServerException(ErrorCode.INTERNAL_ERROR);

	private final ErrorCode errorCode;

	public ErrorCode getErrorCode() {
		return errorCode;
	}
}
