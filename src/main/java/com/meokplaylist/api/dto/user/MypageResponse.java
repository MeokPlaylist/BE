package com.meokplaylist.api.dto.user;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MypageResponse {
    Long feedNum;
    Long followingNum;
    Long followerNum;
    String userNickname;
    String userIntro;
    String profileUrl;
    List<Long> feedId;
    List<String> feedMainPhotoUrls;
}
