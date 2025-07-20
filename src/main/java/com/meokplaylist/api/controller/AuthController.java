package com.meokplaylist.api.controller;

import com.meokplaylist.api.dto.AuthLoginRequest;
import com.meokplaylist.api.dto.AuthSocialLoginRequest;
import com.meokplaylist.domain.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class AuthController {

    private AuthService loginService;

    @GetMapping("/login")
    public void Login(@RequestBody AuthLoginRequest authLoginRequest){

    }

    @GetMapping("/social/login")
    public void SocialLogin(@RequestBody AuthSocialLoginRequest authSocialLoginRequest){

    }

}
