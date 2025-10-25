package com.meokplaylist.api.dto.roadmap;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SaveRoadMapPlaceRequest {
    private Long feedId;
    private String title;
    private List<SaveRoadMapPlaceItem> places;
}

