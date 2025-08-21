package com.meokplaylist.api.controller;


import com.meokplaylist.api.dto.PresignedUrlResponse;
import com.meokplaylist.api.dto.category.FeedCategorySetUpRequest;
import com.meokplaylist.api.dto.feed.FeedCreateRequest;
import com.meokplaylist.domain.service.FeedService;
import lombok.RequiredArgsConstructor;
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
        PresignedUrlResponse presignedUrls =new PresignedUrlResponse(feedService.createFeed(feedCreateRequest, userId));

        return ResponseEntity.ok().body(presignedUrls);
    }

    @GetMapping("/main")
    public ResponseEntity<?> mainFeedElement(@AuthenticationPrincipal Long userId) {

        return ResponseEntity.ok().build();
    }

}