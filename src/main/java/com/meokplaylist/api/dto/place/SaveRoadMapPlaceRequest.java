package com.meokplaylist.api.dto.place;

import com.meokplaylist.api.dto.KakaoSearchResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class SaveRoadMapPlaceRequest {
    private Long feedId;
    private Map<Integer, KakaoSearchResponse.Document> saveRoadMapPlaceInfor;
}
