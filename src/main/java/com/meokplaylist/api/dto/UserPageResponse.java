package com.meokplaylist.api.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class UserPageResponse {
    long feedNum;
    long followingNum;
    long followerNum;
    String userNickname;
    String userIntro;
    String profileUrl;
    List<Long> feedId;
    Map<Integer, List<String>> feedMainPhotoUrls;
    Boolean isMe;
}
