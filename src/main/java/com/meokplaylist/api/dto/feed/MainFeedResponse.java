// 아이템 DTO
package com.meokplaylist.api.dto.feed;

import com.meokplaylist.infra.feed.Feed;
import com.meokplaylist.infra.socialInteraction.Likes;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public record MainFeedResponse(
        String nickName,

        Long feedId,
        String content,
        List<String> hashTag,
        OffsetDateTime createdAt,
        List<String> feedPhotoUrl,

        Map<Long, Boolean> likeBooleanMapByFeedId ,

        long likeCount,
        long commentCount

) {
    public static MainFeedResponse of(
            Feed feed,
            List<String> photoUrls,
            Map<Long, Boolean> likeBooleanMapByFeedId,
            long likeCount,
            long commentCount
    ) {
        return new MainFeedResponse(
                feed.getUser().getNickname(),
                feed.getFeedId(),
                feed.getContent(),
                feed.getHashTag(),      // 컬렉션 매핑에 맞춰 조정
                feed.getCreatedAt(),     // LocalDateTime이면 컨버전해서 넣어도 OK
                photoUrls,
                likeBooleanMapByFeedId,
                likeCount,
                commentCount
        );
    }
}
