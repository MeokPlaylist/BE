package com.meokplaylist.api.dto.user;

import jakarta.validation.constraints.NotEmpty;

public record UserNewPasswordRequest(
        @NotEmpty String password
){}
