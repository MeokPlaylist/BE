package com.meokplaylist.api.controller;

import com.meokplaylist.api.dto.auth.*;
import com.meokplaylist.domain.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.h2.util.json.JSONArray;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Clock;
import java.util.Map;

@RestController
@AllArgsConstructor
public class AuthController {

    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthLoginRequest authLoginRequest){
        authService.login(authLoginRequest);
        Map<String, String> response = Map.of("jwt", "asdasdasdasd");
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/social/login")
    public ResponseEntity<?> socialLogin(@Valid @RequestBody AuthSocialLoginRequest authSocialLoginRequest){

        authService.socialLogin(authSocialLoginRequest);
        Map<String, String> response = Map.of("jwt", "asdasdasdasd");
       return ResponseEntity.ok().body(response);

    }

    @PostMapping("/signUp")
    public ResponseEntity<?> signUp(@Valid @RequestBody AuthSignUpRequest authSignUpRequest) {
        authService.signUp(authSignUpRequest);
        Map<String, String> response = Map.of("jwt", "asdasdasdasd");

        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/emailInspect")
    public ResponseEntity<?> emailInspect(@Valid @RequestBody AuthEmailInspectRequest authEmailInspectRequest){

        authService.emailInspect(authEmailInspectRequest);

        Map<String, String> response = Map.of("isAvailable", "true");
        return ResponseEntity.ok().body(response);
    }



}
