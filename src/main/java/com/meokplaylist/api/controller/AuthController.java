package com.meokplaylist.api.controller;

import com.meokplaylist.api.dto.BooleanResponse;
import com.meokplaylist.api.dto.auth.*;
import com.meokplaylist.domain.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.repository.config.RepositoryNameSpaceHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthLoginRequest authLoginRequest){
        String jwt =authService.login(authLoginRequest);
        AuthJwtResponse response =new AuthJwtResponse(jwt);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/socialLogin")
    public ResponseEntity<?> socialLogin(@Valid @RequestBody AuthSocialLoginRequest authSocialLoginRequest) throws Exception {
        return switch (authSocialLoginRequest.provider()){
            case "kakao" -> ResponseEntity.ok(authService.loginWithKakao(authSocialLoginRequest.token()));
            case "google" -> ResponseEntity.ok(authService.loginWithGoogle(authSocialLoginRequest.token()));
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown provider");
        };
    }

    @PostMapping("/signUp")
    public ResponseEntity<?> signUp(@Valid @RequestBody AuthSignUpRequest authSignUpRequest) {
        String jwt = authService.signUp(authSignUpRequest);
        AuthJwtResponse response =new AuthJwtResponse(jwt);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/email/inspect")
    public ResponseEntity<?> emailInspect(@Valid @RequestBody AuthEmailInspectRequest authEmailInspectRequest){

        BooleanResponse response = new BooleanResponse(authService.emailInspect(authEmailInspectRequest));
        return ResponseEntity.ok().body(response);
    }



}
