package com.meokplaylist.api.dto.feed;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LikeCountDto {
    private Long feedId;
    private Long likeCount;
}
