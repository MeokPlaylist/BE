package com.meokplaylist.api.dto;

import com.meokplaylist.infra.feed.FeedPhotos;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UrlMappedByFeedIdDto {
    private Long feedId;
    private FeedPhotos feedPhotos;
}
