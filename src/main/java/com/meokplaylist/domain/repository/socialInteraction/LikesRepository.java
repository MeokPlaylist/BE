package com.meokplaylist.domain.repository.socialInteraction;


import com.meokplaylist.api.dto.feed.LikeCountDto;
import com.meokplaylist.infra.socialInteraction.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;

public interface LikesRepository extends JpaRepository<Likes,Long> {

    @Query("""
        select f.feedId as feedId, coalesce(count(l.id), 0) as cnt
        from Feed f
        left join Likes l on l.feed = f
         where f.feedId in :feedIds
        group by f.feedId
        """)
    List<LikeCountDto> countByFeedIdsIncludingZero(@Param("feedIds") List<Long> feedIds);
}
