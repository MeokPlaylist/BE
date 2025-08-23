package com.meokplaylist.domain.repository.feed;

import com.meokplaylist.infra.feed.Feed;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.Optional;

public interface FeedRepository extends JpaRepository<Feed,Long> {

    Optional<Feed> findByFeedId(Long feedId);

    Long countByUserUserId(Long userId);

    @EntityGraph(attributePaths = {"user"}) // N+1 줄이기 (필요한 연관만)
    @Query("""
        select f
        from Feed f
        where f.user.userId in (
            select fl.following.userId
            from Follows fl
            where fl.follower.userId = :userId
        )
        order by f.createAt desc, f.id desc
    """)
    Page<Feed> findFollowingFeeds(@Param("userId") Long userId, Pageable pageable);

}
