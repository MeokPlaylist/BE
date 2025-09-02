package com.meokplaylist.api.controller;

import com.meokplaylist.api.dto.UserPageDto;
import com.meokplaylist.api.dto.UserPageResponse;
import com.meokplaylist.api.dto.UserSearchResponse;
import com.meokplaylist.domain.service.SocialInteractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/socialInteraction")
public class SocialInteractionController {

    private final SocialInteractionService socialInteractionService;

    @PostMapping("/follow")
    public ResponseEntity<?> follow(
            @AuthenticationPrincipal Long userId,
            @RequestParam String nickname
    ){
        socialInteractionService.follow(userId,nickname);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/unFollow")
    public ResponseEntity<?> unFollow(
            @AuthenticationPrincipal Long userId,
            @RequestParam String nickname
    ){
        socialInteractionService.unfollow(userId,nickname);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/userPageDistinction")
    public ResponseEntity<?> userPageDistinction(
            @AuthenticationPrincipal Long userId,
            @RequestParam("nickname") String nickName
    ){
        UserPageResponse response=new UserPageResponse(socialInteractionService.userPageDistinction(userId,nickName));

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/searchUser")
    public ResponseEntity<?> searchUser(
            @RequestParam("nickname") String nickname,
            @PageableDefault Pageable pageable
    ){
        UserSearchResponse response=new UserSearchResponse(socialInteractionService.searchUser(nickname,pageable));

        return ResponseEntity.ok().body(response);
    }

}
