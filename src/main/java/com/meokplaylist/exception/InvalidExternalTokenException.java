package com.meokplaylist.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidExternalTokenException extends RuntimeException {
    public InvalidExternalTokenException(String msg) { super(msg); }
}
