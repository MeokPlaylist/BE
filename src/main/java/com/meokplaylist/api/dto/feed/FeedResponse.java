// 아이템 DTO
package com.meokplaylist.api.dto.feed;

import java.time.OffsetDateTime;
import java.util.List;

public record FeedResponse(
        String nickName,
        String content,
        List<String> hashTag,
        OffsetDateTime createdAt,
        List<String> feedPhotoUrl
) {
    public static FeedResponse of(
            com.meokplaylist.infra.feed.Feed feed,
            List<String> photoUrls
    ) {
        return new FeedResponse(
                feed.getUser().getNickname(),
                feed.getContent(),
                feed.getHashTag(),      // 컬렉션 매핑에 맞춰 조정
                feed.getCreatedAt(),     // LocalDateTime이면 컨버전해서 넣어도 OK
                photoUrls
        );
    }
}
