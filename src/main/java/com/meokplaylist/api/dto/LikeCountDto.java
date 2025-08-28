package com.meokplaylist.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LikeCountDto {
    private Long feedId;
    private Long likeCount;
}
