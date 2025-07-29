package com.meokplaylist.api.controller;

import com.meokplaylist.api.dto.user.UserNewPasswordRequest;
import com.meokplaylist.api.dto.user.UserProfileSetupRequest;
import com.meokplaylist.domain.service.ImageService;
import com.meokplaylist.domain.service.UserService;
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


    @PostMapping("/renewalPassword")
    public ResponseEntity<?> renewalPassword(@Valid @RequestBody UserNewPasswordRequest userNewPasswordRequest, @AuthenticationPrincipal Long userId){
        Boolean response = userService.newPassword(userNewPasswordRequest,userId);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/setupProfile")
    public ResponseEntity<?> setupProfile(@AuthenticationPrincipal Long userId, UserProfileSetupRequest userProfileSetupRequest) throws IOException {
        imageService.uploadProfileImage(userProfileSetupRequest.profileImg(), userId);
        return ResponseEntity.ok().build();
    }
}
