package com.creavispace.project.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.creavispace.project.common.dto.response.FailResponseDto;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CreaviCodeException.class)
    public ResponseEntity<FailResponseDto> handleNotFoundException(CreaviCodeException ex) {
        GlobalErrorCode globalErrorCode = ex.getErrorCode();
        return ResponseEntity.status(globalErrorCode.getCode()).body(new FailResponseDto(false, globalErrorCode.getMessage(), globalErrorCode.getCode()));
    }
}
