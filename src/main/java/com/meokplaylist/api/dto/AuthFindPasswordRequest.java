package com.meokplaylist.api.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record AuthFindPasswordRequest(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank LocalDate birthDay
){}
