package com.meokplaylist.api.dto;

import com.meokplaylist.infra.feed.FeedPhotos;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
public class FeedPhotosWithYearDto {
    private Integer year;
    private FeedPhotos feedPhoto;

    public FeedPhotosWithYearDto(OffsetDateTime createdAt, FeedPhotos feedPhoto) {
        this.year = createdAt.getYear();  // 여기서 연도만 뽑기
        this.feedPhoto = feedPhoto;
    }

}
