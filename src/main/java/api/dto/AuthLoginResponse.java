package api.dto;

import lombok.Data;

@Data
public class AuthLoginResponse {
    private String JWTToken;
}
