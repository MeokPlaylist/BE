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
    NOT_FOUND_FEED              (457, "not found feed"),
    NOT_FOUND_USERCATEGORY      (458, "not found userCategory"),
    NOT_FOUND_ROADMAPPLACE      (459,"roadMapPlace not found"),
    NOT_FOUND_FEEDPHOTO         (460, "feedPhoto not found "),
    INVALID_GOOGLEOAUTH_TOKEN  (469,"잘못된 GoogleToken"),

    NOT_FOUND_PLACE    (470, "not found place"),
    LIKE_NOT_FOUND(471,"not found like"),
    NOT_FOUND_ROADMAP(472,"not found roadmap" ),

    EXIST_OBJECT    (475,"exist object"),

    FAILED_TO_UPLOAD_FILE   (480, "failed_to_upload_file"),
    INVALID_PLACE_PAYLOAD   (481,"invalid place payload"),
    SEQUENCE_COUNT_MISMATCH (482,"sequence count mismatch"),
    SEQUENCE_SET_MISMATCH (483, "sequence set mismatch"),

    NOT_HAVE_REFRESHTOKEN  (490, "refreshToken 존재하지 않는다."),
    DONT_HAVE_AUTHORITY  (491,"don't have authority"),

    // 5xx
    INTERNAL_ERROR       (551, "서버 오류");



    private final int status;
    private final String message;
}
