package com.meokplaylist.api.dto.auth;


import io.micrometer.common.lang.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthLoginRequest(
    @NotBlank @Email String email,
    @NotBlank String password,
    @Nullable String providerUid
){}
