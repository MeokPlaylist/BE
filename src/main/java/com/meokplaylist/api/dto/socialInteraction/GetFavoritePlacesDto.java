package com.meokplaylist.api.dto.socialInteraction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public record GetFavoritePlacesDto (
        List<Coordinate> placeCoordinateList
)
{
    public record Coordinate(
            double x,
            double y
    ){ }
}
