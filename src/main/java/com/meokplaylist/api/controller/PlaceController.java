package com.meokplaylist.api.controller;

import com.meokplaylist.api.dto.*;
import com.meokplaylist.api.dto.place.PlaceSearchRequest;
import com.meokplaylist.api.dto.socialInteraction.GetFavoritePlaceResponse;
import com.meokplaylist.api.dto.socialInteraction.RemoveFavoritePlaceDto;
import com.meokplaylist.api.dto.socialInteraction.SaveFavoritePlaceDto;
import com.meokplaylist.domain.service.PlaceService;
import com.meokplaylist.domain.service.RoadMapService;
import com.meokplaylist.domain.service.SocialInteractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/place")
public class PlaceController {
        private final PlaceService placeService;
        private final RoadMapService roadMapService;
        private final SocialInteractionService socialInteractionService;

//        @GetMapping("/pullOutKakao")
//        public ResponseEntity<?> pullOutKakao(@RequestParam("feedId") Long feedId) {
//
//            PullOutKakaoPlaceResponse response=new PullOutKakaoPlaceResponse(placeService.pullOutKakaoPlace(feedId));
//            return ResponseEntity.ok().body(response);
//
//        }

        @PostMapping("/search")
        public ResponseEntity<?> searchPlace(@RequestBody PlaceSearchRequest placeSearchRequest){
            KakaoSearchResponse.Document place = placeService.findPlaceByCategory(placeSearchRequest.getLat(), placeSearchRequest.getLng());
            return ResponseEntity.ok().body(place);
        }

        @PostMapping("/saveFavorite")
        public ResponseEntity<?> SaveFavoritePlace(
                @AuthenticationPrincipal Long userId,
                @RequestBody SaveFavoritePlaceDto request
        ){
            socialInteractionService.SaveFavoritePlace(userId,request);
            return ResponseEntity.ok().build();
        }

        @PostMapping("/saveFavoriteWithPlaceId")
        public ResponseEntity<?> SaveFavoritePlaceWithPlaceId(
                @AuthenticationPrincipal Long userId,
                @RequestParam Long placeId
        ){
            socialInteractionService.SaveFavoritePlaceWithPlaceId(userId,placeId);
            return ResponseEntity.ok().build();
        }



        @PostMapping("/removeFavorite")
        public ResponseEntity<?> removeFavoritePlace(
                @AuthenticationPrincipal Long userId,
                @RequestBody RemoveFavoritePlaceDto request
        ){
            socialInteractionService.removePlace(userId,request);
            return ResponseEntity.ok().build();
        }

        @PostMapping("/removeFavoriteWithPlaceId")
        public ResponseEntity<?> removeFavoritePlaceWithPlaceId(
                @AuthenticationPrincipal Long userId,
                @RequestParam Long placeId
        ){
            socialInteractionService.removePlaceWithPlaceId(userId,placeId);
            return ResponseEntity.ok().build();
        }

        @GetMapping("/getFavorite")
        public ResponseEntity<?> getFavoritePlaces(@AuthenticationPrincipal Long userId){
            GetFavoritePlaceResponse response =new GetFavoritePlaceResponse(socialInteractionService.getFavoritePlaces(userId));

            return ResponseEntity.ok().body(response);
        }
}
