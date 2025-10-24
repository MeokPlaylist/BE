package com.meokplaylist.api.dto.roadmap;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SaveRoadMapPlaceItem {
    private Long roadMapPlaceId;
    private Long selectedPlaceId;      // 후보 선택 시
    private String customPlaceName;    // 직접 입력 시
    private String customAddress;      // 직접 입력 시
}