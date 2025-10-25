package com.meokplaylist.api.dto.roadmap;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class LoadRoadMapInfor {
    private String title;
    private Boolean isMine;
    private String firstDayAndTime;
    private List<LoadRoadMapPlaces> loadRoadMapPlacesList;
}
