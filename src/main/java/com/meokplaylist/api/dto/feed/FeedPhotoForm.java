package com.meokplaylist.api.dto.feed;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

public record FeedPhotoForm(
        MultipartFile photo,
        @NotNull Double latitude,
        @NotNull  Double longitude,
        @NotNull LocalDateTime dayAndTime,
        @NotNull Integer sequence
)
{}
