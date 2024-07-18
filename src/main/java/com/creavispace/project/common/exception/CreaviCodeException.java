package com.creavispace.project.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class CreaviCodeException extends RuntimeException{
    private final String message;
    private final int status;

    public CreaviCodeException(GlobalErrorCode errorCode) {
        this.status = errorCode.getStatus();
        this.message = errorCode.getMessage();
    }

    public CreaviCodeException(String message, int status){
        this.message = message;
        this.status = status;
    }
}
