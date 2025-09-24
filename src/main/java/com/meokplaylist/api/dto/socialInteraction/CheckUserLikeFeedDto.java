package com.meokplaylist.api.dto.socialInteraction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CheckUserLikeFeedDto {
    private Long feedId;
    private Boolean islike;
}
