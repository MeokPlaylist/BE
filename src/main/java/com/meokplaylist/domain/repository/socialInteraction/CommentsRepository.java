package com.meokplaylist.domain.repository.socialInteraction;

import com.meokplaylist.api.dto.feed.CommentCountDto;
import com.meokplaylist.api.dto.feed.LikeCountDto;
import com.meokplaylist.infra.socialInteraction.Comments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentsRepository extends JpaRepository<Comments,Long> {

    long countByFeedFeedId(Long feedId);

    @Query("""
        select f.feedId as feedId, coalesce(count(c.id), 0) as cnt
        from Feed f
        left join Comments c on c.feed = f
         where f.feedId in :feedIds
        group by f.feedId
        """)
    List<CommentCountDto> countByFeedIdsIncludingZero(@Param("feedIds") List<Long> feedIds);
}
