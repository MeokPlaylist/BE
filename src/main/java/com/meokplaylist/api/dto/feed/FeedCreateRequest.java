package com.meokplaylist.api.dto.feed;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record FeedCreateRequest(
        String content,
        MultipartFile profileImg,
        List<String> hashTag,
        List<FeedPhotoRequest> photos
) {}
