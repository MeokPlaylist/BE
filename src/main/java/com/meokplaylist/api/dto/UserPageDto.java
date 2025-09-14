package com.meokplaylist.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@Builder
public class UserPageDto {
    private long feedNum;
    private long followingNum;
    private long followerNum;
    private String userNickname;
    private String userIntro;
    private String profileUrl;
    private Map<Integer, List<Long>> feedIdsGroupedByYear; //년도에 따른
    private Map<String, List<Long>> feedIdsGroupedByRegion; //지역별
    private Map<Long, String> urlMappedByFeedId;
    private Boolean isMe;
    private Boolean isFollowing;
    private Boolean isFollower;

}
