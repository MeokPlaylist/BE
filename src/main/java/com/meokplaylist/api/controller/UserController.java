package com.meokplaylist.api.controller;

import com.meokplaylist.api.dto.Boolean.BooleanRequest;
import com.meokplaylist.api.dto.Boolean.BooleanResponse;
import com.meokplaylist.api.dto.category.CategorySetUpRequest;
import com.meokplaylist.api.dto.user.*;
import com.meokplaylist.domain.service.S3Service;
import com.meokplaylist.domain.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private S3Service s3Service;

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
    public ResponseEntity<?> setupProfile(
            @AuthenticationPrincipal Long userId,
            @RequestBody UserProfileSetupRequest userProfileSetupRequest
    ) {
        UserProfileSetupResponse response=userService.uploadProfileImage(userProfileSetupRequest, userId);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/setupDetailInfo")
    public ResponseEntity<?> setupDetailInfo(@AuthenticationPrincipal Long userId, @Valid @RequestBody UserDetailInfoSetupRequest userDetailInfoSetupRequest){
        userService.DetailSetup(userId,userDetailInfoSetupRequest);
        return ResponseEntity.ok().build();
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

    @PostMapping("/newBCheck")
    public ResponseEntity<?> consentCheck(@AuthenticationPrincipal Long userId){
        userService.consentCheck(userId);
        return ResponseEntity.ok().build();
    }

    //내 페이지
    @GetMapping("/mypage")
    public ResponseEntity<?> mypage(
            @AuthenticationPrincipal Long userId
            ){
        MypageResponse mypageResponse=userService.mypageLoad(userId);
        return ResponseEntity.ok(mypageResponse);
    }


    //팔로우 확인
    @GetMapping("/getFollowerList")
    public ResponseEntity<?> getMyfollowerList(
            @AuthenticationPrincipal Long userId,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ){
        return ResponseEntity.ok(userService.getMyFollowers(userId, pageable));
    }

    @GetMapping("/getFollowingList")
    public ResponseEntity<?> getMyfollowingList(
            @AuthenticationPrincipal Long userId,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ){
        return ResponseEntity.ok(userService.getMyFollowings(userId, pageable));
    }

    @GetMapping("/getOtherUserFollowerList")
    public ResponseEntity<?> getOtherUserfollowerList(
            @RequestParam("nickname") String nickname,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ){
        return ResponseEntity.ok(userService.getOtherUserFollowers(nickname, pageable));
    }

    @GetMapping("/getOtherUserFollowingList")
    public ResponseEntity<?> getOtherUserfollowingList(
            @RequestParam("nickname") String nickname,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ){
        return ResponseEntity.ok(userService.getOtherUserFollowings(nickname, pageable));
    }

    //개인정보
    @GetMapping("/personalInfor")
    public ResponseEntity<?> personalInfor(@AuthenticationPrincipal Long userId){

        PersonalInforResponse personalInfor=userService.getPersonalInfor(userId);

        return ResponseEntity.ok().body(personalInfor);
    }


    @GetMapping("/search")
    public ResponseEntity<?> searchUser(@RequestParam("nickname") String nickname, @PageableDefault Pageable pageable){

        var response=userService.searchUser(nickname,pageable);

        return ResponseEntity.ok().body(response);
    }

    

}
