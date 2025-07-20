package com.meokplaylist.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;

public record AuthSignUpRequest(
    @NotBlank @Email String email,
    @NotBlank String password,
    @NotBlank String name,
    @Past String birthDay
){}