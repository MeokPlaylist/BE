package com.meokplaylist.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 4xx
    INVALID_INPUT        (450,        "잘못된 요청"),
    USER_NOT_FOUND       (451,          "존재하지 않는 회원"),
    USEROAUTH_NOT_FOUND  (452, "존재하지 않는 Oauth"),
    INVALID_OAUTH_TOKEN  (453,"잘못된 Token"),
    INVALID_PASSWORD      (454,"잘못된 password"),
    // 5xx
    INTERNAL_ERROR       (551, "서버 오류");

    private final int status;
    private final String message;
}
