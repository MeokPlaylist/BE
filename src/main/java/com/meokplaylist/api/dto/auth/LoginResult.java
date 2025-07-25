package com.meokplaylist.api.dto.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class LoginResult {
    public enum Status {SUCCESS, NEED_SIGNUP}

    private final Status status;
    private final String token;   // SUCCESS일 때만 채움
    private final Long userId;           // NEED_SIGNUP일 때 가입용 식별자

    public static LoginResult success(String token) {
        return new LoginResult(Status.SUCCESS, token, null);
    }

    public static LoginResult needSignup(Long userId) {
        return new LoginResult(Status.NEED_SIGNUP, null, userId);
    }
}