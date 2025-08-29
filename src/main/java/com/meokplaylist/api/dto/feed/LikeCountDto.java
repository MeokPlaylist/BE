package com.meokplaylist.api.dto.feed;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LikeCountDto {
    private Long feedId;
    private long likeCount;
}
