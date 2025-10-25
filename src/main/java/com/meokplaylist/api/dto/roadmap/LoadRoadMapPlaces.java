package com.meokplaylist.api.dto.roadmap;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoadRoadMapPlaces {
    private Long roadMapPlacesId;
    private String name;
    private String address;
    private String phone;
    private String presignedGetPhotoUrl;
    private Integer dayIndex;
    private Integer orderIndex;

}
