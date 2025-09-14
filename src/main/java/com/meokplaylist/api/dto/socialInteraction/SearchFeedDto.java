package com.meokplaylist.api.dto.socialInteraction;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SearchFeedDto {
    private List<String> categories;
    private List<String> regions;
}
