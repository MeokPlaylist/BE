package com.meokplaylist.api.dto.feed;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class FeedPhotoForm{
    private String fileName;
    private Double latitude;
    private Double longitude;
    private LocalDateTime dayAndTime;
    private Integer sequence;
}
