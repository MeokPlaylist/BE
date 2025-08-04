package com.meokplaylist.exception;

import com.meokplaylist.api.dto.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, String> response = Map.of("message", ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(IllegalAccessException.class)
    public ResponseEntity<Map<String, String>> handleIllegalAccess(IllegalAccessException ex) {
        Map<String, String> response = Map.of("message", ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(BizExceptionHandler.class)
    public ResponseEntity<ErrorResponse> handleBizException(BizExceptionHandler e) {
        ErrorCode errorCode = e.getErrorCode();
        ErrorResponse response = new ErrorResponse(errorCode.getStatus(), errorCode.getMessage());
        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

}
