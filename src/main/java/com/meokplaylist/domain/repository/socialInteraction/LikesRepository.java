package com.meokplaylist.domain.repository.socialInteraction;


import com.meokplaylist.api.dto.feed.LikeCountDto;
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
}
