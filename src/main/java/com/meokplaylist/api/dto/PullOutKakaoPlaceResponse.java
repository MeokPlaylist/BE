package com.meokplaylist.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class PullOutKakaoPlaceResponse {
    private Map<Integer, List<KakaoSearchResponse.Document>> kakaoPlaceInfor;
}
