package com.meokplaylist.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

public class BizExceptionHandler extends RuntimeException{


    private final ErrorCode errorCode;

    public BizExceptionHandler(ErrorCode code) {
        super(code.getMessage());
        this.errorCode = code;
    }
    public BizExceptionHandler(ErrorCode code, String detail) {
        super(detail);
        this.errorCode = code;
    }
    public ErrorCode getErrorCode() {
        return errorCode;
    }

}
