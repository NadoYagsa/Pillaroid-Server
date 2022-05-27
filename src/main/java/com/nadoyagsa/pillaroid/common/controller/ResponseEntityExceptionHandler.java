package com.nadoyagsa.pillaroid.common.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.nadoyagsa.pillaroid.common.dto.ApiResponse;
import com.nadoyagsa.pillaroid.common.exception.BadRequestException;
import com.nadoyagsa.pillaroid.common.exception.ErrorCode;
import com.nadoyagsa.pillaroid.common.exception.NotFoundException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice(annotations = RestController.class)
public class ResponseEntityExceptionHandler {
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	@ExceptionHandler(value = {BadRequestException.class})
	protected ApiResponse badRequestError(BadRequestException ex) {
		log.info(ex.getErrorCode().getDetail());
		return ApiResponse.error(ex.getErrorCode());
	}

	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	@ExceptionHandler(value = {NotFoundException.class})
	protected ApiResponse notFoundError(NotFoundException ex) {
		log.info(ex.getErrorCode().getDetail());
		return ApiResponse.error(ex.getErrorCode());
	}

	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(value = {IOException.class})
	protected ApiResponse IOError(IOException ex) {
		log.error(ex.getMessage());
		return ApiResponse.error(ErrorCode.INTERNAL_ERROR);
	}

	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	@ExceptionHandler(value = {MethodArgumentTypeMismatchException.class})
	protected ApiResponse methodArgumentTypeMismatchError(MethodArgumentTypeMismatchException ex) {
		log.error(ex.getMessage());
		return ApiResponse.error(ErrorCode.BAD_PARAMETER_TYPE);
	}
}
