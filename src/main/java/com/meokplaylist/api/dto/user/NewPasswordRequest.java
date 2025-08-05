package com.meokplaylist.api.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record NewPasswordRequest(
   @NotNull @Positive Long userId,
   @NotBlank String newPassword
) {}
