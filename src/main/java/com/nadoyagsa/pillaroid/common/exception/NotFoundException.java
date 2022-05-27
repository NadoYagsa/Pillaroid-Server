package com.nadoyagsa.pillaroid.common.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NotFoundException extends RuntimeException {
	public static final NotFoundException BARCODE_NOT_FOUND = new NotFoundException(ErrorCode.BARCODE_NOT_FOUND);
	public static final NotFoundException MEDICINE_NOT_FOUND = new NotFoundException(ErrorCode.MEDICINE_NOT_FOUND);
	public static final NotFoundException DATA_NOT_FOUND = new NotFoundException(ErrorCode.DATA_NOT_FOUND);

	private final ErrorCode errorCode;

	public ErrorCode getErrorCode() {
		return errorCode;
	}
}
