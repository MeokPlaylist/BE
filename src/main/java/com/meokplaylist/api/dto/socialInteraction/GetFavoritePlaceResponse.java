package com.meokplaylist.api.dto.socialInteraction;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class GetFavoritePlaceResponse {
    List<GetFavoritePlacesDto.Coordinate> placeCoordinates;
}

