package com.meokplaylist.api.controller;

import com.meokplaylist.api.dto.RecommendRestaurantRequest;
import com.meokplaylist.api.dto.UserPageResponse;
import com.meokplaylist.domain.service.SocialInteractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @GetMapping("/initRecommendRestaurant")
    public ResponseEntity<Map<String, List<String>>> recommendRestaurant(
            @AuthenticationPrincipal Long userId
    ) {
        Map<String, List<String>> items = socialInteractionService
                .initRecommendRestaurant(userId)
                .block();

        return ResponseEntity.ok(items);
    }
    @PostMapping("/recommendRestaurant")
    public ResponseEntity<Map<String,List<String>>> recommendRestaurant(
            @RequestBody RecommendRestaurantRequest request
    ) {
        Map<String, List<String>> items = socialInteractionService
                .recommendRestaurant(request)  // Mono<List<String>>
                .block();
        return ResponseEntity.ok(items);
    }
}
