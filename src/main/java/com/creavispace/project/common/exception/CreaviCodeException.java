package com.creavispace.project.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class CreaviCodeException extends RuntimeException{
    private GlobalErrorCode errorCode;

    public CreaviCodeException(GlobalErrorCode errorCode) {
        this.errorCode = errorCode;
        log.info("에러 = {}, code = {}", errorCode.getMessage(), errorCode.getCode());
    }
}
