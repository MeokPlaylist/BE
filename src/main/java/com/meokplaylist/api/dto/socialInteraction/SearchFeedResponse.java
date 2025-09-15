package com.meokplaylist.api.dto.socialInteraction;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class SearchFeedResponse {
    private List<Map<Long, String>> urlsMappedByFeedIds;
}
