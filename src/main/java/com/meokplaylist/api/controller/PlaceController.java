package com.meokplaylist.api.controller;

import com.meokplaylist.api.dto.*;
import com.meokplaylist.domain.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

        @GetMapping("/test")
        public ResponseEntity<?> test(@RequestParam("category") String category,@RequestParam("x") double x, @RequestParam("y") double y){
            Test list=new Test(placeService.findAllPlaceByCategory(category,x,y));
            return ResponseEntity.ok().body(list);
        }


        @GetMapping
        public ResponseEntity<?> callInRoadMapPlace(@RequestParam("feedId") Long feedId){

            CallInRoadMapResponse response =new CallInRoadMapResponse(placeService.callInRoadMap(feedId));

            return ResponseEntity.ok().body(response);
        }





}
