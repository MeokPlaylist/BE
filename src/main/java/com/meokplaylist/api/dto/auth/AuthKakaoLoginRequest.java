package com.meokplaylist.api.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record AuthKakaoLoginRequest(
        @NotBlank String accessToken,
        String refreshToken // refreshToken은 SDK에서 항상 내려오는 건 아니라 optional 처리
) {}
