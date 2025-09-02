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
    long feedNum;
    long followingNum;
    long followerNum;
    String userNickname;
    String userIntro;
    String profileUrl;
    Map<Integer, List<Long>> feedIdsGroupedByYear; //년도에 따른
    Map<String, List<Long>> feedIdsGroupedByRegion; //지역별
    Map<Long, String> urlMappedByFeedId;
    Boolean isMe;
}
