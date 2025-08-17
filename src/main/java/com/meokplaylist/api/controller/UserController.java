package com.meokplaylist.api.controller;

import com.meokplaylist.api.dto.BooleanRequest;
import com.meokplaylist.api.dto.BooleanResponse;
import com.meokplaylist.api.dto.category.CategorySetUpRequest;
import com.meokplaylist.api.dto.StringUrlResponse;
import com.meokplaylist.api.dto.user.*;
import com.meokplaylist.domain.service.ImageService;
import com.meokplaylist.domain.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private ImageService imageService;


    @PostMapping("/find")
    public ResponseEntity<?> findUser(@Valid @RequestBody FindUserRequest findUserRequest){
        FindUserResponse findUserResponse=new FindUserResponse(userService.findUser(findUserRequest));
        return ResponseEntity.ok().body(findUserResponse);
    }

    @PostMapping("/renewalPassword")
    public ResponseEntity<?> renewalPassword(@Valid @RequestBody NewPasswordRequest newPasswordRequest) {
        BooleanResponse booleanResponse =new BooleanResponse(userService.newPassword(newPasswordRequest));
        return ResponseEntity.ok().body(booleanResponse);
    }

    @PostMapping("/setupProfile")
    public ResponseEntity<?> setupProfile( //여기 check 해봐야함
            @AuthenticationPrincipal Long userId,
            @ModelAttribute UserProfileSetupRequest userProfileSetupRequest) throws IOException {
        imageService.uploadProfileImage(userProfileSetupRequest.profileImg(), userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/setupDetailInfo")
    public ResponseEntity<?> setupDetailInfo(@Valid @RequestBody UserDetailInfoSetupRequest userDetailInfoSetupRequest,@AuthenticationPrincipal Long userId){
        userService.DetailSetup(userId,userDetailInfoSetupRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/consentAgree")
    public ResponseEntity<?> consentUpload(@RequestBody BooleanRequest booleanRequest, @AuthenticationPrincipal Long userId){
        BooleanResponse booleanResponse = new BooleanResponse(userService.consentUpload(booleanRequest,userId));
        return ResponseEntity.ok().body(booleanResponse);
    }

    @PostMapping("/categorySet")
    public ResponseEntity<?> categorySet(@Valid @RequestBody CategorySetUpRequest categorySetUpRequest, @AuthenticationPrincipal Long userId){
        userService.categorySetUp(categorySetUpRequest,userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/newBCheck")
    public ResponseEntity<?> consentCheck(@AuthenticationPrincipal Long userId){
        userService.consentCheck(userId);
        return ResponseEntity.ok().build();
    }



}
