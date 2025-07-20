package api.controller;

import api.dto.AuthLoginRequest;
import api.dto.AuthSocialLoginRequest;
import domain.repository.service.LoginService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class LoginController {

    private LoginService loginService;

    @GetMapping("/login")
    public void Login(@RequestBody AuthLoginRequest authLoginRequest){

    }

    @GetMapping("/social/login")
    public void SocialLogin(@RequestBody AuthSocialLoginRequest authSocialLoginRequest){

    }

}
