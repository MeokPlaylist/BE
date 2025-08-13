package com.meokplaylist.api.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthJwtResponse {
    private String jwt;
}
