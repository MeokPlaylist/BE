package com.meokplaylist.api.controller;

import com.meokplaylist.api.dto.auth.*;
import com.meokplaylist.domain.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.meokplaylist.api.dto.auth.LoginResult.Status.NEED_SIGNUP;
import static com.meokplaylist.api.dto.auth.LoginResult.Status.SUCCESS;

@RestController
@AllArgsConstructor
public class AuthController {

    private AuthService authService;

    @GetMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthLoginRequest authLoginRequest){
        LoginResult result = authService.login(authLoginRequest);

        switch (result.getStatus()) {
            case SUCCESS -> {
                return ResponseEntity.ok().build();
            }
            case NEED_SIGNUP -> {
                /* 428  */
                return ResponseEntity.status(HttpStatus.PRECONDITION_REQUIRED).build();
            }
            default -> throw new IllegalStateException("Unexpected value: " + result.getStatus());
        }
    }

    @GetMapping("/social/login")
    public ResponseEntity<?> socialLogin(@Valid @RequestBody AuthSocialLoginRequest authSocialLoginRequest){
        LoginResult result = authService.socialLogin(authSocialLoginRequest);

        switch (result.getStatus()) {
            case SUCCESS -> {
                return ResponseEntity.ok().build();
            }
            case NEED_SIGNUP -> {
                /* 428  */
                return ResponseEntity.status(HttpStatus.PRECONDITION_REQUIRED).build();
            }
            default -> throw new IllegalStateException("Unexpected value: " + result.getStatus());
        }
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
