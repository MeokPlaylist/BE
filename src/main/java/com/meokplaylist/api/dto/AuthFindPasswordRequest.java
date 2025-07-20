package com.meokplaylist.api.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthFindPasswordRequest(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank String birthDay
){}
