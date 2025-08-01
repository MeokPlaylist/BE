package com.meokplaylist.api.controller;

import com.meokplaylist.api.dto.BooleanRequest;
import com.meokplaylist.api.dto.BooleanResponse;
import com.meokplaylist.api.dto.category.CategorySetUpRequest;
import com.meokplaylist.api.dto.StringUrlResponse;
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
        BooleanResponse booleanResponse=new BooleanResponse(userService.newPassword(userNewPasswordRequest,userId));
        return ResponseEntity.ok().body(booleanResponse);
    }

    @PostMapping("/setupProfile")
    public ResponseEntity<?> setupProfile(@AuthenticationPrincipal Long userId, UserProfileSetupRequest userProfileSetupRequest) throws IOException {
        StringUrlResponse stringUrlResponse=new StringUrlResponse(imageService.uploadProfileImage(userProfileSetupRequest.profileImg(), userId));
        return ResponseEntity.ok().body(stringUrlResponse);
    }

    @PostMapping("/consentAgree")
    public ResponseEntity<?> consentUpload(@Valid @RequestBody BooleanRequest booleanRequest, @AuthenticationPrincipal Long userId){
        BooleanResponse booleanResponse = new BooleanResponse(userService.consentUpload(booleanRequest,userId));
        return ResponseEntity.ok().body(booleanResponse);
    }

    @PostMapping("/categorySet")
    public ResponseEntity<?> categorySet(@Valid @RequestBody CategorySetUpRequest categorySetUpRequest, @AuthenticationPrincipal Long userId){
        userService.categorySetUp(categorySetUpRequest,userId);
        return ResponseEntity.ok().build();
    }




}
