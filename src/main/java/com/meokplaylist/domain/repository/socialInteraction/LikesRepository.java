package com.meokplaylist.domain.repository.socialInteraction;


import com.meokplaylist.api.dto.feed.LikeCountDto;
import com.meokplaylist.api.dto.socialInteraction.CheckUserLikeFeedDto;
import com.meokplaylist.infra.socialInteraction.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;

public interface LikesRepository extends JpaRepository<Likes,Long> {

    @Query("select new com.meokplaylist.api.dto.feed.LikeCountDto(f.feedId, count(l)) " +
            "from Feed f left join f.likes l " +
            "where f.feedId in :feedIds " +
            "group by f.feedId")
    List<LikeCountDto> countLikesByFeedIds(@Param("feedIds") List<Long> feedIds);

    long countLikesByFeedFeedId(Long feedId);

    @Query("""
    SELECT CASE WHEN COUNT(l) > 0 THEN TRUE ELSE FALSE END
    FROM Likes l
    WHERE l.feed.feedId = :feedId
      AND l.user.userId = :userId
    """)
    Boolean existsByFeedFeedIdAndUserUserId(Long feedId, Long userId);


    @Query("""
    select new com.meokplaylist.api.dto.socialInteraction.CheckUserLikeFeedDto(
        f.feedId,
        case when count(l) > 0 then true else false end
    )
    from Feed f
    left join f.likes l on l.user.userId = :userId
    where f.feedId in :feedIds
    group by f.feedId
    """)
    List<CheckUserLikeFeedDto> findByFeedIdsAndUserId(@Param(("feedIds")) List<Long> feedId, @Param("userId") Long userId);
}
