package com.nadoyagsa.pillaroid.common;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.nadoyagsa.pillaroid.common.dto.ApiResponse;
import com.nadoyagsa.pillaroid.common.exception.ProjectException;

@RestControllerAdvice(annotations = RestController.class)
public class ResponseEntityExceptionHandler {
	@ExceptionHandler(value = {ProjectException.class})
	protected ApiResponse projectError(ProjectException ex) {
		return ApiResponse.error(ex.getErrorCode());
	}
}
