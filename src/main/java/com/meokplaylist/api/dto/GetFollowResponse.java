package com.meokplaylist.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetFollowResponse {
    private String nickname;
    private String profileImgKey;
    private String introduction;
}
