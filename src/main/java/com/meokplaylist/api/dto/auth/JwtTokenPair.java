package com.meokplaylist.api.dto.auth;

public record JwtTokenPair(String accessToken, String refreshToken) {
    public static JwtTokenPair of(String access, String refresh) {
        return new JwtTokenPair(access, refresh);
    }
}
