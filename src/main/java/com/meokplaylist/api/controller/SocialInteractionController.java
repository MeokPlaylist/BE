package com.meokplaylist.api.controller;

import com.meokplaylist.api.dto.RecommendRestaurantRequest;
import com.meokplaylist.api.dto.RecommendRestaurantResponse;
import com.meokplaylist.api.dto.UserPageResponse;
import com.meokplaylist.domain.service.SocialInteractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("/recommendRestaurant")
    public ResponseEntity<RecommendRestaurantResponse> recommendRestaurant(
            @RequestBody RecommendRestaurantRequest request
    ) {
        List<String> items = socialInteractionService
                .recommendRestaurant(request)
                .block();

        RecommendRestaurantResponse body = new RecommendRestaurantResponse(items);
        System.out.println(body);
        return ResponseEntity.ok(body);
    }


}
