package com.meokplaylist.api.controller;

import com.meokplaylist.api.dto.AuthFindPasswordRequest;
import com.meokplaylist.api.dto.AuthNewPasswordRequest;
import com.meokplaylist.api.dto.UserProfileSetupRequest;
import com.meokplaylist.domain.service.ImageService;
import com.meokplaylist.domain.service.UserService;
import com.meokplaylist.infra.Users;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private ImageService imageService;

    @PostMapping("/findPassword")
    public ResponseEntity<?> emailInspect(@Valid @RequestBody AuthFindPasswordRequest authFindPasswordRequest) throws IllegalAccessException {
        Users user = userService.findPassword(authFindPasswordRequest);
        return ResponseEntity.ok(user);

    }

    @PostMapping("/renewalPassword")
    public ResponseEntity<?> renewalPassword(@Valid @RequestBody AuthNewPasswordRequest authNewPasswordRequest, Users user){
        userService.newPassword(authNewPasswordRequest,user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/setupProfile")
    public ResponseEntity<?> setupProfile(@AuthenticationPrincipal Long userId, UserProfileSetupRequest userProfileSetupRequest) throws IOException {
        imageService.uploadProfileImage(userProfileSetupRequest.profileImg(), userId);
        return ResponseEntity.ok().build();
    }
}
