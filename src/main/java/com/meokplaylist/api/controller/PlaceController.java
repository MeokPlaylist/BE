package com.meokplaylist.api.controller;

import com.meokplaylist.api.dto.*;
import com.meokplaylist.api.dto.place.PlaceSearchRequest;
import com.meokplaylist.api.dto.place.CallInRoadMapResponse;
import com.meokplaylist.api.dto.place.SaveRoadMapPlaceRequest;
import com.meokplaylist.api.dto.place.SearchPlaceDto;
import com.meokplaylist.api.dto.place.SearchPlaceResponse;
import com.meokplaylist.domain.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/place")
public class PlaceController {
        private final PlaceService placeService;

        @GetMapping("/pullOutKakao")
        public ResponseEntity<?> pullOutKakao(@RequestParam("feedId") Long feedId) {

            PullOutKakaoPlaceResponse response=new PullOutKakaoPlaceResponse(placeService.pullOutKakaoPlace(feedId));
            return ResponseEntity.ok().body(response);

        }

        @PostMapping("saveRoadMap")
        public ResponseEntity<?> saveRoadMapPlace(SaveRoadMapPlaceRequest saveRoadMapPlaceRequest){

            placeService.saveRoadMapPlace(saveRoadMapPlaceRequest);

            return ResponseEntity.ok().build();

        }


        @GetMapping("/callInRoadMap")
        public ResponseEntity<?> callInRoadMapPlace(@RequestParam("feedId") Long feedId){

            CallInRoadMapResponse response =new CallInRoadMapResponse(placeService.callInRoadMap(feedId));

            return ResponseEntity.ok().body(response);
        }

        @PostMapping("/search")
        public ResponseEntity<?> searchPlace(@RequestBody PlaceSearchRequest placeSearchRequest){
            KakaoSearchResponse.Document place = placeService.findPlaceByCategory(placeSearchRequest.getLat(), placeSearchRequest.getLng());
            return ResponseEntity.ok().body(place);
        }
}
