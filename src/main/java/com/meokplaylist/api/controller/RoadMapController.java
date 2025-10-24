package com.meokplaylist.api.controller;

import com.meokplaylist.api.dto.roadmap.SaveRoadMapPlaceRequest;
import com.meokplaylist.api.dto.roadmap.RoadMapCandidateDto;
import com.meokplaylist.domain.service.RoadMapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/roadmap")
public class RoadMapController {
    private final RoadMapService roadMapService;

    @GetMapping("/create")
    public ResponseEntity<?> createRoadMap(@RequestParam("feedId")Long feedId, @AuthenticationPrincipal Long userId){
        List<RoadMapCandidateDto> response = roadMapService.createRoadMap(feedId, userId);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveRoadMap (@RequestBody SaveRoadMapPlaceRequest request){
        roadMapService.saveRoadMapPlace(request);
        return ResponseEntity.ok().build();
    }

}