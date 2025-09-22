package com.meokplaylist.api.controller;

import com.meokplaylist.api.dto.Boolean.BooleanResponse;
import com.meokplaylist.api.dto.auth.*;
import com.meokplaylist.domain.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthLoginRequest authLoginRequest){
        JwtTokenPair pair = authService.login(authLoginRequest);
        return ResponseEntity.ok().body(pair);
    }

    @PostMapping("/socialLogin/google")
    public ResponseEntity<?> socialLogin(@Valid @RequestBody AuthGoogleLoginRequest authGoogleLoginRequest) throws Exception {
        JwtTokenPair pair = authService.loginWithGoogle(authGoogleLoginRequest.idToken());
        return ResponseEntity.ok().body(pair);
    }

    @PostMapping("/socialLogin/kakao")
    public ResponseEntity<?> socialLogin(@Valid @RequestBody AuthKakaoLoginRequest authKakaoLoginRequest) throws Exception {
        JwtTokenPair pair = authService.loginWithKakao(authKakaoLoginRequest.accessToken(), authKakaoLoginRequest.refreshToken());
        return ResponseEntity.ok().body(pair);
    }

    @PostMapping("/signUp")
    public ResponseEntity<?> signUp(@Valid @RequestBody AuthSignUpRequest authSignUpRequest) {
        JwtTokenPair pair = authService.signUp(authSignUpRequest);
        return ResponseEntity.ok().body(pair);
    }

    @PostMapping("/emailInspect")
    public ResponseEntity<?> emailInspect(@Valid @RequestBody AuthEmailInspectRequest authEmailInspectRequest){

        BooleanResponse response = new BooleanResponse(authService.emailInspect(authEmailInspectRequest));
        return ResponseEntity.ok().body(response);
    }


    @GetMapping("/nicknameDuplicateCheck")
    public ResponseEntity<?> nicknameDuplicateCheck(@RequestParam("nickname") String nickname){
        BooleanResponse response=new BooleanResponse(authService.nicknameDuplicateCheck(nickname));
        return ResponseEntity.ok().body(response);
    }



}
