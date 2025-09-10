package com.meokplaylist.api.dto.socialInteraction;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class SearchFeedResponse {
    private Map<Long, List<String>> feedUrlsAndSocialMap;
}
