// 아이템 DTO
package com.meokplaylist.api.dto.feed;

import com.meokplaylist.infra.feed.Feed;
import com.meokplaylist.infra.socialInteraction.Likes;

import java.time.OffsetDateTime;
import java.util.List;

public record MainFeedResponse(
        String nickName,
        String content,
        List<String> hashTag,
        OffsetDateTime createdAt,
        List<String> feedPhotoUrl,

        long likeCount,
        long commentCount

) {
    public static MainFeedResponse of(
            Feed feed,
            List<String> photoUrls,
            long likeCount,
            long commentCount
    ) {
        return new MainFeedResponse(
                feed.getUser().getNickname(),
                feed.getContent(),
                feed.getHashTag(),      // 컬렉션 매핑에 맞춰 조정
                feed.getCreatedAt(),     // LocalDateTime이면 컨버전해서 넣어도 OK
                photoUrls,
                likeCount,
                commentCount
        );
    }
}
