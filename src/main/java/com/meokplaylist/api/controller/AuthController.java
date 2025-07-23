package com.meokplaylist.api.controller;

import com.meokplaylist.api.dto.*;
import com.meokplaylist.domain.service.AuthService;
import com.meokplaylist.domain.service.ImageService;
import com.meokplaylist.infra.Users;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@AllArgsConstructor
public class AuthController {

    private AuthService authService;

    @GetMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthLoginRequest authLoginRequest){

        return ResponseEntity.ok().build();
    }

    @GetMapping("/social/login")
    public void socialLogin(@RequestBody AuthSocialLoginRequest authSocialLoginRequest){

    }

    @PostMapping("/signUp")
    public ResponseEntity<?> signUp(@Valid @RequestBody AuthSignUpRequest authSignUpRequest) {
        authService.signUp(authSignUpRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/emailInspect")
    public ResponseEntity<?> emailInspect(@Valid @RequestBody AuthEmailInspectRequest authEmailInspectRequest){
        authService.emailInspect(authEmailInspectRequest);
        return ResponseEntity.ok().build();
    }



}
