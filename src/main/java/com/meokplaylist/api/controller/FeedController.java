package com.meokplaylist.api.controller;

import com.meokplaylist.api.dto.BooleanResponse;
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

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createFeed(
           // @AuthenticationPrincipal Long userId,
            @RequestParam Long userId, //테스트용
            @ModelAttribute FeedCreateRequest feedCreateRequest,
            @ModelAttribute FeedCategorySetUpRequest feedCategorySetUpRequest
    ){

        BooleanResponse booleanResponse=new BooleanResponse(feedService.createFeed(feedCreateRequest,feedCategorySetUpRequest,userId));

        return ResponseEntity.ok(booleanResponse);
    }
}
