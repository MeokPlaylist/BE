package com.meokplaylist.api.controller;

import com.meokplaylist.domain.service.SocialInteractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/socialInteraction")
public class SocialInteractionController {

    private final SocialInteractionService socialInteractionService;

    @PostMapping("/follow")
    public void follow(
            @AuthenticationPrincipal Long userId,
            @RequestParam String nickname
    ){
        socialInteractionService.follow(userId,nickname);
    }

    @PostMapping("/unFollow")
    public void unFollow(
            @AuthenticationPrincipal Long userId,
            @RequestParam String nickname
    ){
        socialInteractionService.unfollow(userId,nickname);
    }

    @GetMapping("/userDistinction")
    public void userDistinction(
            @AuthenticationPrincipal Long userId,
            String nickName
    ){
        socialInteractionService.userDistinction(userId,nickName);

    }

}
