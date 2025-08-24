package com.meokplaylist.api.controller;


import com.meokplaylist.api.dto.PresignedUrlResponse;
import com.meokplaylist.api.dto.category.FeedCategorySetUpRequest;
import com.meokplaylist.api.dto.feed.FeedCreateRequest;
import com.meokplaylist.api.dto.feed.FeedResponse;
import com.meokplaylist.api.dto.feed.SlicedResponse;
import com.meokplaylist.api.dto.mainFeedResponse;
import com.meokplaylist.domain.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
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
        PresignedUrlResponse presignedPutUrls =new PresignedUrlResponse(feedService.createFeed(feedCreateRequest, userId));

        return ResponseEntity.ok().body(presignedPutUrls);
    }


    @GetMapping("/main")
    public ResponseEntity<SlicedResponse<FeedResponse>> mainFeeds(
            @AuthenticationPrincipal Long userId,
            @PageableDefault(size = 2) Pageable pageable
    ) {
        var slice = feedService.mainFeedSelectSlice(userId, pageable);
        return ResponseEntity.ok(feedService.mainFeedSelectSlice(userId, pageable));
    }
}