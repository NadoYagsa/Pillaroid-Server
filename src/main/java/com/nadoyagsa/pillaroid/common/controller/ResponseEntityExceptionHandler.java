package com.nadoyagsa.pillaroid.common.controller;

import java.io.IOException;
import java.time.format.DateTimeParseException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nadoyagsa.pillaroid.common.exception.ForbiddenException;
import com.nadoyagsa.pillaroid.common.exception.InternalServerException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.nadoyagsa.pillaroid.common.dto.ApiResponse;
import com.nadoyagsa.pillaroid.common.exception.BadRequestException;
import com.nadoyagsa.pillaroid.common.exception.ErrorCode;
import com.nadoyagsa.pillaroid.common.exception.NotFoundException;
import com.nadoyagsa.pillaroid.common.exception.UnauthorizedException;

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

	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	@ExceptionHandler(value = {MethodArgumentTypeMismatchException.class})
	protected ApiResponse methodArgumentTypeMismatchError(MethodArgumentTypeMismatchException ex) {
		log.info(ex.getMessage());
		return ApiResponse.error(ErrorCode.BAD_PARAMETER_TYPE);
	}

	// 유효성 검사 실패 시
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	@ExceptionHandler(value = {MethodArgumentNotValidException.class})
	protected ApiResponse methodArgumetNotValidError(MethodArgumentNotValidException ex) {
		log.info(ex.getMessage());
		return ApiResponse.error(ErrorCode.BAD_PARAMETER, ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
	}

	// JSON 직렬화, 역직렬화 실패 (ex. 타입 변환 실패)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	@ExceptionHandler(value = {JsonProcessingException.class})
	protected ApiResponse jsonProcessingError(JsonProcessingException ex) {
		log.info(ex.getMessage());
		return ApiResponse.error(ErrorCode.BAD_PARAMETER);
	}

	@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
	@ExceptionHandler(value = {UnauthorizedException.class})
	protected ApiResponse unauthorizedError(UnauthorizedException ex) {
		log.info(ex.getErrorCode().getDetail());
		return ApiResponse.error(ex.getErrorCode());
	}

	@ResponseStatus(value = HttpStatus.FORBIDDEN)
	@ExceptionHandler(value = {ForbiddenException.class})
	protected ApiResponse forbiddenError(ForbiddenException ex) {
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
	@ExceptionHandler(value = {InternalServerException.class})
	protected ApiResponse IOError(InternalServerException ex) {
		log.info(ex.getErrorCode().getDetail());
		return ApiResponse.error(ex.getErrorCode());
	}

	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(value = {IOException.class})
	protected ApiResponse IOError(IOException ex) {
		log.error(ex.getMessage());
		return ApiResponse.error(ErrorCode.INTERNAL_ERROR);
	}
}
