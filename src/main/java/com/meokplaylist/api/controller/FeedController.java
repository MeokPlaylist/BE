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

    @GetMapping("/main")
    public ResponseEntity<?> mainFeedElement(@AuthenticationPrincipal Long userId){

        return ResponseEntity.ok().build();
    }

}


    /*
     createFeed Dto
     content : 오늘 점심
     hashTag : 학식
     hashTag : 맛집
     photos[0].file : (파일 선택)
     photos[0].lat : 37.123
     photos[0].lng : 127.456
     photos[0].placeName : 정문
     photos[0].order : 0
     photos[1].file : (파일 선택)
     photos[1].lat : 37.124
     photos[1].lng : 127.457
     photos[1].placeName : 학식
     photos[1].order : 1

     */
