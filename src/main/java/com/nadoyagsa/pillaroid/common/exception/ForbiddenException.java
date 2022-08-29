package com.nadoyagsa.pillaroid.common.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ForbiddenException extends Exception {
	public static final ForbiddenException deleteForbidden = new ForbiddenException(ErrorCode.DELETE_FORBIDDEN);

	private final ErrorCode errorCode;

	public ErrorCode getErrorCode() {
		return errorCode;
	}
}
