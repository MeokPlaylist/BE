package com.meokplaylist.api.dto.user;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class MypageResponse {
    long feedNum;
    long followingNum;
    long followerNum;
    String userNickname;
    String userIntro;
    String profileUrl;
    Map<Integer, List<String>> urlGroupedByYear; //년도에 따른

    Map<Long, String> urlMappedByFeedId;

}
