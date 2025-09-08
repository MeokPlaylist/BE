package com.meokplaylist.domain.repository.socialInteraction;

import com.meokplaylist.api.dto.feed.CommentCountDto;
import com.meokplaylist.api.dto.feed.LikeCountDto;
import com.meokplaylist.api.dto.socialInteraction.GetFeedCommentsDto;
import com.meokplaylist.infra.socialInteraction.Comments;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentsRepository extends JpaRepository<Comments,Long> {

    long countByFeedFeedId(Long feedId);

    @Query("select new com.meokplaylist.api.dto.feed.CommentCountDto(f.feedId, count(c)) " +
            "from Feed f left join f.comments c " +
            "where f.feedId in :feedIds " +
            "group by f.feedId")
    List<CommentCountDto> findCommentCountsByFeedIds(@Param("feedIds") List<Long> feedIds);



    @EntityGraph(attributePaths = {"author"})
    @Query("""
           select new com.meokplaylist.api.dto.socialInteraction.GetFeedCommentsDto(
               u.profileImgKey,
               u.nickname,
               c.createdAt,
               c.content
           )
           from Comments c
           join c.author u
           where c.feed.feedId = :feedId
           order by c.createdAt desc
           """)
    Slice<GetFeedCommentsDto> findCommentByFeedId(@Param("feedId") Long feedId, Pageable pageable);



}
