package com.meokplaylist.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 4xx
    ERROR_CODE           (449, "잘못된 동작"),
    INVALID_INPUT        (450,        "잘못된 요청"),
    USER_NOT_FOUND       (451,          "존재하지 않는 회원"),
    USEROAUTH_NOT_FOUND  (452, "존재하지 않는 Oauth"),

    INVALID_OAUTH_TOKEN  (453,"잘못된 Token"),
    INVALID_GOOGLEOAUTH_TOKEN  (454,"잘못된 GoogleToken"),

    INVALID_PASSWORD      (454,"잘못된 password"),
    CATEGORY_NOT_FOUND    (470, "caregory not found"),
    NOT_HAVE_REFRESHTOKEN  (490, "refreshToken 존재하지 않는다."),
    // 5xx
    INTERNAL_ERROR       (551, "서버 오류"),
    INVALID_OAUTH_PROVIDER(455,"제공하지 않는 소셜");

    private final int status;
    private final String message;
}
