package com.meokplaylist.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import reactor.core.publisher.Mono;

@Data
@AllArgsConstructor
public class RecommendRestaurantResponse {
    List<String> recommendRestaurant;
}
