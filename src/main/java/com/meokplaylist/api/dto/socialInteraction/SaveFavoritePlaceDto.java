package com.meokplaylist.api.dto.socialInteraction;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data  
@AllArgsConstructor
public class SaveFavoritePlaceDto {
    private double lat;
    private double lng;

}
