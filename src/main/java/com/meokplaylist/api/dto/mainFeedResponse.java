package com.meokplaylist.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class mainFeedResponse {

    private String nickname;

    //feed에 관한 정보들
    private String content;
    private List<String> hashTag;
    private OffsetDateTime createdAt;

    private List<String> feedPhotoUrl;

    //여기에 추가로 좋아요랑 댓글 수 들어가야함
}
