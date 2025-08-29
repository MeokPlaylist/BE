package com.meokplaylist.api.dto.feed;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentCountDto {
    private Long feedId;
    private Long commentCount;
}
