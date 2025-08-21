package com.meokplaylist.api.dto.feed;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class FeedCreateRequest {
    private String content;
    private List<String> hashTag;
    private List<FeedPhotoForm> photos;
    private List<String> categories;
    private List<String> regions;
}
