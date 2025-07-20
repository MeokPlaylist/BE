package com.meokplaylist.api.controller;

import com.meokplaylist.api.dto.*;
import com.meokplaylist.domain.service.AuthService;
import com.meokplaylist.infra.Users;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/findPassword")
    public ResponseEntity<?> emailInspect(@Valid @RequestBody AuthFindPasswordRequest authFindPasswordRequest) throws IllegalAccessException {
        Users user = authService.findPassword(authFindPasswordRequest);
        return ResponseEntity.ok(user);

    }

    @PostMapping("/renewalPassword")
    public ResponseEntity<?> renewalPassword(@Valid @RequestBody AuthNewPasswordRequest authNewPasswordRequest, Users user){
        authService.newPassword(authNewPasswordRequest,user);
        return ResponseEntity.ok().build();
    }


}
