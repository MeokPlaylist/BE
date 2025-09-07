package com.meokplaylist.api.controller;


import com.meokplaylist.api.dto.Boolean.BooleanResponse;
import com.meokplaylist.api.dto.feed.ModifyFeedCategoryDto;
import com.meokplaylist.api.dto.feed.ModifyFeedContentDto;
import com.meokplaylist.api.dto.presignedUrl.PresignedPutListUrlResponse;
import com.meokplaylist.api.dto.feed.FeedCreateRequest;
import com.meokplaylist.api.dto.feed.MainFeedResponse;
import com.meokplaylist.api.dto.SlicedResponse;
import com.meokplaylist.domain.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feed")
public class FeedController {
    private final FeedService feedService;

    @PostMapping("/create")
    public ResponseEntity<?> createFeed(
            @AuthenticationPrincipal Long userId,
            @RequestBody FeedCreateRequest feedCreateRequest
    ) {
        PresignedPutListUrlResponse presignedPutUrls =new PresignedPutListUrlResponse(feedService.createFeed(feedCreateRequest, userId));

        return ResponseEntity.ok().body(presignedPutUrls);
    }


    @GetMapping("/main")
    public ResponseEntity<SlicedResponse<MainFeedResponse>> mainFeeds(
            @AuthenticationPrincipal Long userId,
            @PageableDefault(size = 2) Pageable pageable
    ) {
        var slice = feedService.mainFeedSelectSlice(userId, pageable);
        return ResponseEntity.ok(slice);
    }


    @PostMapping("/delete")
    public ResponseEntity<?> deleteFeed(@RequestParam("feedId") Long feedId,@AuthenticationPrincipal Long userId){
        feedService.deleteFeed(feedId,userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/modifyCategory")
    public ResponseEntity<?> modifyFeedCategory(
            @RequestBody ModifyFeedCategoryDto request,
            @AuthenticationPrincipal Long userId
    ){

        feedService.modifyFeedCategory(request,userId);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/modifyContent")
    public ResponseEntity<?> modifyFeedContent(
            @RequestBody ModifyFeedContentDto request,
            @AuthenticationPrincipal Long userId
    ){
        feedService.modifyFeedContent(request,userId);
        return ResponseEntity.ok().build();

    }


}