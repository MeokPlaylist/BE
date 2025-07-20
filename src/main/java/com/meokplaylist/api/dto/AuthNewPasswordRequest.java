package com.meokplaylist.api.dto;

import jakarta.validation.constraints.NotEmpty;

public record AuthNewPasswordRequest(
        @NotEmpty String password
){}
