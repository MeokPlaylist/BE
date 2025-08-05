package com.meokplaylist.api.dto.user;

import jakarta.validation.constraints.NotBlank;

public record UserDetailInfoSetupRequest(
        @NotBlank String nickname,
        String introduction
)
{}
