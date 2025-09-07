package com.meokplaylist.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Duration;
import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
public class GetFeedCommentsResponse {

    private Long feedId;
    private String nickname;
    private String profileIngUrl;
    private Duration durationTime; //댓글 작성시간
    private Boolean isparents;//대댓글 유무
}
