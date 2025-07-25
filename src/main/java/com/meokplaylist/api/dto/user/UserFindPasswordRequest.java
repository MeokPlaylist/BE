package com.meokplaylist.api.dto.user;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record UserFindPasswordRequest(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank LocalDate birthDay
){}
