package com.nadoyagsa.pillaroid.common.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UnauthorizedException extends RuntimeException {
    public static final UnauthorizedException UNAUTHORIZED_USER = new UnauthorizedException(ErrorCode.UNAUTHORIZED_USER);

    private final ErrorCode errorCode;

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
