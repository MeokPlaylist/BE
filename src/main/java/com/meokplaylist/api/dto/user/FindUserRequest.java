package com.meokplaylist.api.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record FindUserRequest(

        @NotBlank String name,
        @NotBlank @Email String email
){}
