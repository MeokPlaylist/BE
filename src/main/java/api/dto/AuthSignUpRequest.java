package api.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class AuthSignUpRequest {
    private String email;
    private String password;
    private String name;
    private String birthDay;
}
