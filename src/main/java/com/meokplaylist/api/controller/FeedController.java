package com.meokplaylist.api.controller;


import com.meokplaylist.api.dto.PresignedPutListUrlResponse;
import com.meokplaylist.api.dto.feed.FeedCreateRequest;
import com.meokplaylist.api.dto.feed.MainFeedResponse;
import com.meokplaylist.api.dto.feed.SlicedResponse;
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
}