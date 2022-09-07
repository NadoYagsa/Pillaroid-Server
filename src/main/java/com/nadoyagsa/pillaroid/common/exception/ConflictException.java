package com.nadoyagsa.pillaroid.common.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConflictException extends RuntimeException {
	public static final ConflictException DATA_CONFLICT = new ConflictException(ErrorCode.DATA_CONFLICT);

	private final ErrorCode errorCode;

	public ErrorCode getErrorCode() {
		return errorCode;
	}
}
