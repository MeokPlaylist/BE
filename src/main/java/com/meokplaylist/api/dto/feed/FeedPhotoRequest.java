package com.meokplaylist.api.dto.feed;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record FeedPhotoRequest(
        @NotNull Double latitude,
        @NotNull  Double longitude,
        @NotNull LocalDateTime dayAndTime,
        @NotNull Integer sequence
)
{}
