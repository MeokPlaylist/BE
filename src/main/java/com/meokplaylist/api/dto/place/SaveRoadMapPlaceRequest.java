package com.meokplaylist.api.dto.place;

import com.meokplaylist.api.dto.KakaoSearchResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class SaveRoadMapPlaceRequest {
    private Long feedId;
    private String title;
    private Map<Long, Long> saveRoadMapPlaceInfor;
}
