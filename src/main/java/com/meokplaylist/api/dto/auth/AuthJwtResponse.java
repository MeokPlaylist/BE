package com.meokplaylist.api.dto.auth;

public record AuthJwtResponse(
        String accessToken,
        String refreshToken
) {
    public static AuthJwtResponse of(JwtTokenPair pair) {
        return new AuthJwtResponse(pair.accessToken(), pair.refreshToken());
    }
}
