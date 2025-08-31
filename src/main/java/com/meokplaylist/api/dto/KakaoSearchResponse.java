package com.meokplaylist.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record KakaoSearchResponse(
        Meta meta,
        List<Document> documents
) {
    public record Meta(
            @JsonProperty("total_count") int totalCount,
            @JsonProperty("pageable_count") int pageableCount,
            @JsonProperty("is_end") boolean isEnd
    ) {}

    public record Document(
            String id,
            @JsonProperty("place_name") String placeName,
            String phone,
            @JsonProperty("address_name") String addressName,
            @JsonProperty("road_address_name") String roadAddressName,
            @JsonProperty("place_url") String placeUrl,
            @JsonProperty("category_group_code") String categoryGroupCode,
            @JsonProperty("category_group_name") String categoryGroupName
    ) {}
}
