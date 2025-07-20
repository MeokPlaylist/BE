package com.meokplaylist.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record AuthEmailInspectRequest(
        @NotEmpty @Email String email
){}
