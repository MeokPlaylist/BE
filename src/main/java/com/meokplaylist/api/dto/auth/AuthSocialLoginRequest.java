package com.meokplaylist.api.dto.auth;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthSocialLoginRequest(
        @NotBlank String provider,
        @NotBlank String idToken
) {}
