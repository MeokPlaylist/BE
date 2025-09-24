package com.meokplaylist.api.dto.place;

import com.meokplaylist.api.dto.KakaoSearchResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SearchPlaceResponse {
    private List<KakaoSearchResponse.Document> placeList;
}
