package com.meokplaylist.api.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDate;

public record UserNewPasswordRequest(

        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank LocalDate birthDay,
        @NotEmpty String password
){}
