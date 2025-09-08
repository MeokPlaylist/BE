package com.meokplaylist.api.dto.socialInteraction;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
public class GetFeedCommentsDto {
    private String profileImgUrl;
    private String nickname;
    private OffsetDateTime duration;
    private String content;

}
