package com.meokplaylist.api.controller;

import com.meokplaylist.api.dto.BooleanResponse;
import com.meokplaylist.api.dto.category.FeedCategorySetUpRequest;
import com.meokplaylist.api.dto.feed.FeedCreateRequest;
import com.meokplaylist.api.dto.feed.FeedPhotoRequest;
import com.meokplaylist.domain.service.FeedService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feed")
public class FeedController {
    private final FeedService feedService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createFeed(
            @AuthenticationPrincipal Long userId,
            @RequestPart("content") String content,
            @RequestPart("profileImg") MultipartFile profileImg,
            @RequestPart("hashTag") List<String> hashTag,
            @RequestPart("photos") List<FeedPhotoRequest> photos,
            FeedCategorySetUpRequest feedCategorySetUpRequest
    ){
        FeedCreateRequest feedCreateRequest =new FeedCreateRequest(content, profileImg, hashTag, photos);
        BooleanResponse booleanResponse=new BooleanResponse(feedService.createFeed(feedCreateRequest,feedCategorySetUpRequest,userId));

        return ResponseEntity.ok(booleanResponse);
    }
}
