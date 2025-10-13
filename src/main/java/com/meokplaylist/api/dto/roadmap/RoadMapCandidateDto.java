package com.meokplaylist.api.dto.roadmap;

import com.meokplaylist.infra.place.Places;

import java.util.List;

public record RoadMapCandidateDto(
        Long roadMapPlaceId,
        String photoUrl,
        String dateTime,
        Integer dayIndex,
        Integer orderIndex,
        List<PlaceCandidate> candidates
) {
    public record PlaceCandidate(
            Long placeId,
            String placeName,
            String address
    ) {
        public static PlaceCandidate from(Places place) {
            return new PlaceCandidate(
                    place.getId(),
                    place.getName(),
                    place.getAddressName()
            );
        }
    }
}
