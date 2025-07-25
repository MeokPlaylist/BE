package com.meokplaylist.api.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record AuthSignUpRequest(
    @NotBlank @Email String email,
    @NotBlank String password,
    @NotBlank String name,
    @Past LocalDate birthDay
){}