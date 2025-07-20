package com.meokplaylist.api.dto;

public record AuthSignUpRequest {
    @NotBlank String email;
    private String password;
    private String name;
    private String birthDay;
}