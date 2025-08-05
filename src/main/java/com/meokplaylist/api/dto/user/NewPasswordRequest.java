package com.meokplaylist.api.dto.user;

import jakarta.validation.constraints.NotBlank;

public record NewPasswordRequest(
   @NotBlank Long userId,
   @NotBlank String newPassword
) {}
