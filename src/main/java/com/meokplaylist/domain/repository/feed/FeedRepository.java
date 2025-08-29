package com.meokplaylist.domain.repository.feed;

import com.meokplaylist.infra.feed.Feed;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Optional;

public interface FeedRepository extends JpaRepository<Feed,Long> {

    Optional<Feed> findByFeedId(Long feedId);

    long countByUserUserId(Long userId);

    @EntityGraph(attributePaths = {"user"})
    @Query("""
        select f
        from Feed f
        where f.user.userId in (
            select fl.following.userId
            from Follows fl
            where fl.follower.userId = :userId
        )
        order by f.createdAt desc, f.feedId desc
    """)
    Slice<Feed> findFollowingFeeds(@Param("userId") Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"user"})
    @Query("""
        SELECT f
        FROM Feed f
        JOIN FeedCategory fc ON fc.feed = f
        WHERE fc.category.id IN :categoryIds
        GROUP BY f.id
        ORDER BY COUNT(fc.id) DESC, MAX(f.createdAt) DESC, f.id DESC
    """)
    Slice<Feed> findCategoryIds(@Param("categoryIds") List<Long> categoryIds, Pageable pageable);

}
