package com.meokplaylist.api.dto;

import com.meokplaylist.infra.feed.FeedPhotos;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class FeedPhotosWithLocalDto {
    private String localName;
    private FeedPhotos feedPhoto;
}
