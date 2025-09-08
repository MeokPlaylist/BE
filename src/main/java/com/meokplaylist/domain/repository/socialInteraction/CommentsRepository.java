package com.meokplaylist.domain.repository.socialInteraction;

import com.meokplaylist.api.dto.feed.CommentCountDto;
import com.meokplaylist.api.dto.feed.LikeCountDto;
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



    // 부모가 없는 1뎁스 댓글(루트)만
    @EntityGraph(attributePaths = {"author"})
    Slice<Comments> findByFeedFeedIdAndParentIsNullOrderByCreatedAtAsc(Long feedId, Pageable pageable);

    // 특정 댓글의 대댓글
    @EntityGraph(attributePaths = {"author"})
    Slice<Comments> findByParentCommentIdOrderByCreatedAtAsc(Long parentId, Pageable pageable);
}
