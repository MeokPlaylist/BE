package com.meokplaylist.api.dto.auth;


import jakarta.validation.constraints.NotBlank;

public record AuthGoogleLoginRequest(
        @NotBlank String idToken
) {}
