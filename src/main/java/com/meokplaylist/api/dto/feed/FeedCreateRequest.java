package com.meokplaylist.api.dto.feed;

import java.util.List;

public record FeedCreateRequest(
        String content,
        List<String> hashTag,
        List<FeedPhotoForm> photos
) {}
