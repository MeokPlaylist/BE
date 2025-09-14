package com.meokplaylist.api.controller;

import com.meokplaylist.api.dto.socialInteraction.RecommendRestaurantRequest;
import com.meokplaylist.api.dto.UserPageResponse;
import com.meokplaylist.api.dto.socialInteraction.SearchFeedResponse;
import com.meokplaylist.api.dto.socialInteraction.WriteFeedCommentsDto;
import com.meokplaylist.domain.service.SocialInteractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
            @RequestParam("nickname") String nickname
    ){
        socialInteractionService.follow(userId,nickname);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/unFollow")
    public ResponseEntity<?> unFollow(
            @AuthenticationPrincipal Long userId,
            @RequestParam("nickname") String nickname
    ){
        socialInteractionService.unfollow(userId,nickname);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/userPageDistinction")
    public ResponseEntity<?> userPageDistinction(
            @AuthenticationPrincipal Long userId,
            @RequestParam("nickname") String nickName
    ){
        UserPageResponse response = new UserPageResponse(socialInteractionService.userPageDistinction(userId,nickName));
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

    @GetMapping("/getFeedComments")
    public ResponseEntity<?> getFeedComments(
            @RequestParam("feedId") Long feedId,
            @PageableDefault Pageable pageable
    ){
        var slice=socialInteractionService.getFeedComments(feedId, pageable);

        return ResponseEntity.ok().body(slice);
    }

    @PostMapping("/writeFeedComments")
    public ResponseEntity<?> writeFeedComments(WriteFeedCommentsDto request){
        socialInteractionService.writeFeedComments(request);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/searchFeed")
    public ResponseEntity<SearchFeedResponse> searchFeed(
            @AuthenticationPrincipal Long userId,

            @PageableDefault Pageable pageable
    ) {
        SearchFeedResponse response = socialInteractionService.searchFeed(userId, pageable);
        return ResponseEntity.ok(response);
    }

}
