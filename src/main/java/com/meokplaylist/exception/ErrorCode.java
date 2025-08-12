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
    CONSENT_NOT_FOUND               (453, "not found consent"),

    INVALID_PASSWORD      (454,"잘못된 password"),
    DONT_HAVE_NICKNAME          (455, "don't have nickname"),
    USERCATEGORY_NOT_FONUD      (456, "userCategory not found"),
    NOT_FOUND_FEED              (457, "not found feed"),
    INVALID_OAUTH_TOKEN  (460,"잘못된 Token"),
    INVALID_GOOGLEOAUTH_TOKEN  (461,"잘못된 GoogleToken"),

    CATEGORY_NOT_FOUND    (470, "caregory not found"),

    FAILED_TO_UPLOAD_FILE   (480, "failed_to_upload_file"),
    NOT_HAVE_REFRESHTOKEN  (490, "refreshToken 존재하지 않는다."),

    // 5xx
    INTERNAL_ERROR       (551, "서버 오류"),
    INVALID_OAUTH_PROVIDER(455,"제공하지 않는 소셜"),


    //10XX

    CHECH_OK    (1000,"newB chech ok");
    private final int status;
    private final String message;
}
