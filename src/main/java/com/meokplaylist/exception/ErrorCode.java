package com.meokplaylist.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 4xx
    INVALID_INPUT        (HttpStatus.BAD_REQUEST,        "잘못된 요청"),
    USER_NOT_FOUND       (HttpStatus.NOT_FOUND,          "존재하지 않는 회원"),
    USEROAUTH_NOT_FOUND  (HttpStatus.NOT_FOUND, "존재하지 않는 Oauth"),
    ACCESS_DENIED        (HttpStatus.FORBIDDEN,          "권한 없음"),

    // 5xx
    INTERNAL_ERROR       (HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류");

    private final HttpStatus status;
    private final String message;
}
