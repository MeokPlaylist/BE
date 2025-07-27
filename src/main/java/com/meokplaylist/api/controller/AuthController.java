package com.meokplaylist.api.controller;

import com.meokplaylist.api.dto.auth.*;
import com.meokplaylist.domain.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Clock;
import java.util.Map;

@RestController
@AllArgsConstructor
public class AuthController {

    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthLoginRequest authLoginRequest){
        String jwt =authService.login(authLoginRequest);
        return ResponseEntity.ok().body(jwt);
    }

    @PostMapping("/social/login")
    public ResponseEntity<?> socialLogin(@Valid @RequestBody AuthSocialLoginRequest authSocialLoginRequest) throws Exception {

        String jwt=authService.socialLogin(authSocialLoginRequest);
       return ResponseEntity.ok().body(jwt);

    }

    @PostMapping("/signUp")
    public ResponseEntity<?> signUp(@Valid @RequestBody AuthSignUpRequest authSignUpRequest) {
        String jwt = authService.signUp(authSignUpRequest);
        return ResponseEntity.ok().body(jwt);
    }

    @PostMapping("/emailInspect")
    public ResponseEntity<?> emailInspect(@Valid @RequestBody AuthEmailInspectRequest authEmailInspectRequest){

        Boolean response =authService.emailInspect(authEmailInspectRequest);
        return ResponseEntity.ok().body(response);
    }



}
