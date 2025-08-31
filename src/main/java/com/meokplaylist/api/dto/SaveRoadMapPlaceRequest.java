package com.meokplaylist.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class SaveRoadMapPlaceRequest {
    private Long feedId;
    private Map<Integer, KakaoSearchResponse.Document> saveRoadMapPlaceInfor;
}
