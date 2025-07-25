package com.meokplaylist.api.dto.auth;

import lombok.Data;

@Data
public class AuthLoginResponse {
    private String JWTToken;
}
